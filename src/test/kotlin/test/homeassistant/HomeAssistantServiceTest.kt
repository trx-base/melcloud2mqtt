package test.homeassistant

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import homeassistant.HomeAssistantConfig
import homeassistant.HomeAssistantService
import homeassistant.data.HaConfig
import homeassistant.data.HaDevice
import io.micronaut.scheduling.annotation.Scheduled
import io.micronaut.serde.ObjectMapper
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import melcloud2mqtt.service.MelcloudService
import melcloud2mqtt.view.DeviceView
import melcloud2mqtt.view.ListDevicesView
import mqtt.Publisher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

@ExtendWith(MockKExtension::class)
class HomeAssistantServiceTest {

    private val log: Logger = LoggerFactory.getLogger(HomeAssistantServiceTest::class.java)

    @InjectMockKs
    lateinit var homeAssistantService: HomeAssistantService

    @RelaxedMockK
    lateinit var publisher: Publisher

    @RelaxedMockK
    lateinit var melcloudService: MelcloudService

    @RelaxedMockK
    lateinit var homeAssistantConfig: HomeAssistantConfig

    var discoveryTopic: String? = null

    @BeforeEach
    fun setUp() {
        val deviceView = DeviceView("1878694284", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
        every { melcloudService.listDevices() } returns ListDevicesView(listOf(deviceView))
        every { homeAssistantConfig.discoveryTopic } returns "homeassistnat-test"

        discoveryTopic = homeAssistantConfig.discoveryTopic
    }

    @Test
    fun shouldPublishToHomeAssistantTopic_whenConfig() {
        homeAssistantService.config()
        verify {
            publisher.publish(
                eq(true),
                eq("$discoveryTopic/sensor/melcloud2mqtt_1878694284/FlowTemperature/config"),
                null,
                any(),
            )
        }
    }

    @Test
    fun shouldPublishFlowTemperature_whenConfig() {
        val haConfigSlot = slot<HaConfig>()
        every {
            publisher.publish(
                any(),
                eq("$discoveryTopic/sensor/melcloud2mqtt_1878694284/FlowTemperature/config"),
                any(),
                capture(haConfigSlot),
            )
        } returns mockk()

        homeAssistantService.config()
        assertThat(haConfigSlot.captured.device.identifiers).containsExactly("melcloud2mqtt_1878694284")
        assertThat(haConfigSlot.captured.device.name).isEqualTo("MELCloud ")
        assertThat(haConfigSlot.captured.name).isEqualTo("FlowTemperature")
        assertThat(haConfigSlot.captured.deviceClass).isEqualTo("temperature")
        assertThat(haConfigSlot.captured.stateClass).isEqualTo("measurement")
        assertThat(haConfigSlot.captured.stateTopic).isEqualTo("$discoveryTopic/sensor/melcloud2mqtt_1878694284/state")
        assertThat(haConfigSlot.captured.uniqueId).isEqualTo("melcloud2mqtt_1878694284_FlowTemperature")
        assertThat(haConfigSlot.captured.unitOfMeasurement).isEqualTo("°C")
        assertThat(haConfigSlot.captured.enabledByDefault).isTrue()
        assertThat(haConfigSlot.captured.valueTemplate).isEqualTo("{{ value_json.FlowTemperature}}")
    }

    @Test
    fun shouldPublish_whenGivenListDeviceViewFromMelcloudService_whenConfig() {
        val expectedId = UUID.randomUUID().toString()
        val deviceView = DeviceView(expectedId, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
        every { melcloudService.listDevices() } returns ListDevicesView(listOf(deviceView))

        val haConfigSlot = slot<HaConfig>()
        every { publisher.publish(any(), any(), any(), capture(haConfigSlot)) } returns mockk()

        homeAssistantService.config()
        assertThat(haConfigSlot.captured.device.identifiers).containsExactly("melcloud2mqtt_$expectedId")
    }

    @Test
    fun shouldPublishFlowTemperatureSensor_whenGivenFlowTemperature() {
        val deviceId = UUID.randomUUID().toString()
        val deviceView = DeviceView(deviceId, "Name", "42", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
        every { melcloudService.listDevices() } returns ListDevicesView(listOf(deviceView))

        homeAssistantService.config()
        val expectedHaConfig = HaConfig(
            HaDevice(listOf("melcloud2mqtt_$deviceId"), "MELCloud Name"),
            "FlowTemperature",
            "temperature",
            "measurement",
            "°C",
            "melcloud2mqtt_${deviceId}_FlowTemperature",
            "$discoveryTopic/sensor/melcloud2mqtt_$deviceId/state",
            true,
            "{{ value_json.FlowTemperature}}",
        )
        verify {
            publisher.publish(
                true,
                "$discoveryTopic/sensor/melcloud2mqtt_$deviceId/FlowTemperature/config",
                null,
                expectedHaConfig,
            )
        }
    }

    @Test
    fun shouldUseSnakeCaseStrategy_whenHaConfigToJson() {
        val haConfig = HaConfig(HaDevice(listOf("val"), "val"), "val", "val", "val", "val", "val", "val", false, "val")
        assertThat(
            ObjectMapper.getDefault().writeValueAsString(haConfig),
        ).isEqualTo("""{"device":{"identifiers":["val"],"name":"val"},"name":"val","device_class":"val","state_class":"val","unit_of_measurement":"val","unique_id":"val","state_topic":"val","enabled_by_default":false,"value_template":"val"}""")
    }

    @Test
    fun shouldPublishDeleteFlowTemperature_whenDeleteConfig_andGivenFlowTemperature() {
        val deviceId = UUID.randomUUID().toString()
        val deviceView = DeviceView(deviceId, "", "42", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
        every { melcloudService.listDevices() } returns ListDevicesView(listOf(deviceView))

        homeAssistantService.deleteConfig()
        verify {
            publisher.publish(
                true,
                "$discoveryTopic/sensor/melcloud2mqtt_$deviceId/FlowTemperature/config",
                null,
                null,
            )
        }
    }

    @Test
    fun shouldPublishReturnTemperatureSensor_whenGivenReturnTemperature() {
        val deviceId = UUID.randomUUID().toString()
        val deviceView = DeviceView(
            deviceId,
            "Name",
            "",
            "",
            "42",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
        )

        every { melcloudService.listDevices() } returns ListDevicesView(listOf(deviceView))

        homeAssistantService.config()
        val expectedHaConfig = HaConfig(
            HaDevice(listOf("melcloud2mqtt_$deviceId"), "MELCloud Name"),
            "ReturnTemperature",
            "temperature",
            "measurement",
            "°C",
            "melcloud2mqtt_${deviceId}_ReturnTemperature",
            "$discoveryTopic/sensor/melcloud2mqtt_$deviceId/state",
            true,
            "{{ value_json.ReturnTemperature}}",
        )
        verify {
            publisher.publish(
                true,
                "$discoveryTopic/sensor/melcloud2mqtt_$deviceId/ReturnTemperature/config",
                null,
                expectedHaConfig,
            )
        }
    }

    @Test
    fun shouldHaveScheduledAnnotation_whenGivenConfigMethod() {
        val annotations = HomeAssistantService::config.annotations
        assertThat(annotations).containsExactly(
            Scheduled(
                initialDelay = "10s",
                scheduler = "scheduled",
                cron = "",
                zoneId = "",
                fixedDelay = "",
                fixedRate = "",
            ),
        )
    }

    @Test
    fun shouldPublishState_whenUpdateState() {
        val deviceId = UUID.randomUUID().toString()
        val deviceView =
            DeviceView(deviceId, "", "", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14")
        every { melcloudService.listDevices() } returns ListDevicesView(listOf(deviceView))

        homeAssistantService.updateState()
        verify {
            publisher.publish(
                false,
                "$discoveryTopic/sensor/melcloud2mqtt_$deviceId/state",
                null,
                deviceView,
            )
        }
    }

    @Test
    fun shouldHaveScheduledAnnotation_whenGivenUpdateStateMethod() {
        val annotations = HomeAssistantService::updateState.annotations
        assertThat(annotations).containsExactly(
            Scheduled(
                initialDelay = "20s",
                scheduler = "scheduled",
                cron = "",
                zoneId = "",
                fixedDelay = "5m",
                fixedRate = "",
            ),
        )
    }

    @Test
    fun shouldUsePascalCaseStrategy_whenDeviceViewToJson() {
        val deviceView = DeviceView(
            "val",
            "val",
            "val",
            "val",
            "val",
            "val",
            "val",
            "val",
            "val",
            "val",
            "val",
            "val",
            "val",
            "val",
            "val",
            "val",
            "val",
        )
        log.info(ObjectMapper.getDefault().writeValueAsString(deviceView))
        assertThat(
            ObjectMapper.getDefault().writeValueAsString(deviceView),
        ).isEqualTo("""{"Id":"val","Name":"val","Type":"val","FlowTemperature":"val","ReturnTemperature":"val","CurrentEnergyConsumed":"val","CurrentEnergyProduced":"val","DailyHeatingEnergyConsumed":"val","DailyHeatingEnergyProduced":"val","DailyCoolingEnergyConsumed":"val","DailyCoolingEnergyProduced":"val","DailyHotWaterEnergyConsumed":"val","DailyHotWaterEnergyProduced":"val","SetHeatFlowTemperatureZone1":"val","SetCoolFlowTemperatureZone1":"val","HeatPumpFrequency":"val","OutdoorTemperature":"val"}""")
    }

    @Test
    fun shouldPublishSetHeatFlowTemperatureZone1Sensor_whenGivenReturnTemperature() {
        val deviceId = UUID.randomUUID().toString()
        val deviceView = DeviceView(deviceId, "Name", "", "", "", "", "", "", "", "", "", "", "", "42", "", "", "")
        every { melcloudService.listDevices() } returns ListDevicesView(listOf(deviceView))

        homeAssistantService.config()
        val expectedHaConfig = HaConfig(
            HaDevice(listOf("melcloud2mqtt_$deviceId"), "MELCloud Name"),
            "SetHeatFlowTemperatureZone1",
            "temperature",
            "measurement",
            "°C",
            "melcloud2mqtt_${deviceId}_SetHeatFlowTemperatureZone1",
            "$discoveryTopic/sensor/melcloud2mqtt_$deviceId/state",
            true,
            "{{ value_json.SetHeatFlowTemperatureZone1}}",
        )
        verify {
            publisher.publish(
                true,
                "$discoveryTopic/sensor/melcloud2mqtt_$deviceId/SetHeatFlowTemperatureZone1/config",
                null,
                expectedHaConfig,
            )
        }
    }

    @Test
    fun shouldPublishOutdoorTemperatureSensor_whenGivenReturnTemperature() {
        val deviceId = UUID.randomUUID().toString()
        val deviceView = DeviceView(deviceId, "Name", "", "", "", "", "", "", "", "", "", "", "", "42", "", "", "")
        every { melcloudService.listDevices() } returns ListDevicesView(listOf(deviceView))

        homeAssistantService.config()
        val expectedHaConfig = HaConfig(
            HaDevice(listOf("melcloud2mqtt_$deviceId"), "MELCloud Name"),
            "OutdoorTemperature",
            "temperature",
            "measurement",
            "°C",
            "melcloud2mqtt_${deviceId}_OutdoorTemperature",
            "$discoveryTopic/sensor/melcloud2mqtt_$deviceId/state",
            true,
            "{{ value_json.OutdoorTemperature}}",
        )
        verify {
            publisher.publish(
                true,
                "$discoveryTopic/sensor/melcloud2mqtt_$deviceId/OutdoorTemperature/config",
                null,
                expectedHaConfig,
            )
        }
    }

    @Test
    fun shouldAddPrefixToClientId_whenConfig() {
        val deviceId = UUID.randomUUID().toString()
        val deviceView = DeviceView(deviceId, "", "", "", "", "", "", "", "", "", "", "", "", "42", "", "", "")

        val slot = slot<HaConfig>()
        every { homeAssistantService.publisher.publish(any(), any(), any(), capture(slot)) } returns Unit
        every { melcloudService.listDevices() } returns ListDevicesView(listOf(deviceView))

        homeAssistantService.config()
        assertThat(slot.captured.device.identifiers.first()).isEqualTo("melcloud2mqtt_$deviceId")
    }
}
