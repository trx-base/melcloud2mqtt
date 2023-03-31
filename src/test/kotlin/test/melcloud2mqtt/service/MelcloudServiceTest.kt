package test.melcloud2mqtt.service

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import melcloud2mqtt.client.MelcloudHttpClient
import melcloud2mqtt.client.model.Building
import melcloud2mqtt.client.model.DeviceGeneral
import melcloud2mqtt.client.model.DeviceSpecific
import melcloud2mqtt.client.model.SetAtw
import melcloud2mqtt.client.model.Structure
import melcloud2mqtt.service.MelcloudService
import melcloud2mqtt.service.SessionService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class MelcloudServiceTest {

    @InjectMockKs
    lateinit var mqttService: MelcloudService

    @RelaxedMockK
    lateinit var melcloudHttpClient: MelcloudHttpClient

    @RelaxedMockK
    lateinit var sessionService: SessionService

    @BeforeEach
    fun setUp() {
        every { melcloudHttpClient.listDevices(any()) } returns emptyArray()
    }

    @Test
    fun shouldReturnSetAtwView_GivenByMelcloudHttpClient_whenSetHeatFlowTemperatureZone1() {
        every { melcloudHttpClient.setHeatFlowTemperatureZone1(any(), any(), any()) } returns SetAtw("42")
        val setAtwView = mqttService.setHeatFlowTemperatureZone1("", "")
        assertThat(setAtwView.setHeatFlowTemperatureZone1).isEqualTo("42")
    }

    @Test
    fun shouldProvideValueToHttpClient_whenSetHeatFlowTemperatureZone1() {
        val value = UUID.randomUUID().toString()
        mqttService.setHeatFlowTemperatureZone1("", value)
        verify { melcloudHttpClient.setHeatFlowTemperatureZone1(any(), any(), eq(value)) }
    }

    @Test
    fun shouldProvideDeviceIdToHttpClient_whenSetHeatFlowTemperatureZone1() {
        val deviceId = UUID.randomUUID().toString()
        mqttService.setHeatFlowTemperatureZone1(deviceId, "")
        verify { melcloudHttpClient.setHeatFlowTemperatureZone1(any(), eq(deviceId), any()) }
    }

    @Test
    fun shouldMapToListDevicesView_whenGivenBuildingArrayByMelcloudHttpClient_whenListDevices() {
        val deviceId = UUID.randomUUID().toString()
        val deviceType = UUID.randomUUID().toString()
        val flowTemperature = UUID.randomUUID().toString()
        val returnTemperature = UUID.randomUUID().toString()
        val currentEnergyConsumed = UUID.randomUUID().toString()
        val currentEnergyProduced = UUID.randomUUID().toString()
        val dailyHeatingEnergyConsumed = UUID.randomUUID().toString()
        val dailyHeatingEnergyProduced = UUID.randomUUID().toString()
        val dailyCoolingEnergyConsumed = UUID.randomUUID().toString()
        val dailyCoolingEnergyProduced = UUID.randomUUID().toString()
        val dailyHotWaterEnergyConsumed = UUID.randomUUID().toString()
        val dailyHotWaterEnergyProduced = UUID.randomUUID().toString()
        val setHeatFlowTemperatureZone1 = UUID.randomUUID().toString()
        val setCoolFlowTemperatureZone1 = UUID.randomUUID().toString()
        val heatPumpFrequency = UUID.randomUUID().toString()
        val outdoorTemperature = UUID.randomUUID().toString()

        every { melcloudHttpClient.listDevices(any()) } returns arrayOf(
            Building(
                Structure(
                    arrayOf(
                        DeviceGeneral(
                            deviceId,
                            DeviceSpecific(
                                deviceType,
                                flowTemperature,
                                returnTemperature,
                                currentEnergyConsumed,
                                currentEnergyProduced,
                                dailyHeatingEnergyConsumed,
                                dailyHeatingEnergyProduced,
                                dailyCoolingEnergyConsumed,
                                dailyCoolingEnergyProduced,
                                dailyHotWaterEnergyConsumed,
                                dailyHotWaterEnergyProduced,
                                setHeatFlowTemperatureZone1,
                                setCoolFlowTemperatureZone1,
                                heatPumpFrequency,
                                outdoorTemperature,
                            ),
                        ),
                    ),
                ),
            ),
        )
        val listDevicesView = mqttService.listDevices()
        assertThat(listDevicesView.devices.first().id).isEqualTo(deviceId)
        assertThat(listDevicesView.devices.first().type).isEqualTo(deviceType)
        assertThat(listDevicesView.devices.first().flowTemperature).isEqualTo(flowTemperature)
        assertThat(listDevicesView.devices.first().returnTemperature).isEqualTo(returnTemperature)
        assertThat(listDevicesView.devices.first().currentEnergyConsumed).isEqualTo(currentEnergyConsumed)
        assertThat(listDevicesView.devices.first().currentEnergyProduced).isEqualTo(currentEnergyProduced)
        assertThat(listDevicesView.devices.first().dailyHeatingEnergyConsumed).isEqualTo(dailyHeatingEnergyConsumed)
        assertThat(listDevicesView.devices.first().dailyHeatingEnergyProduced).isEqualTo(dailyHeatingEnergyProduced)
        assertThat(listDevicesView.devices.first().dailyCoolingEnergyConsumed).isEqualTo(dailyCoolingEnergyConsumed)
        assertThat(listDevicesView.devices.first().dailyCoolingEnergyProduced).isEqualTo(dailyCoolingEnergyProduced)
        assertThat(listDevicesView.devices.first().dailyHotWaterEnergyConsumed).isEqualTo(dailyHotWaterEnergyConsumed)
        assertThat(listDevicesView.devices.first().dailyHotWaterEnergyProduced).isEqualTo(dailyHotWaterEnergyProduced)
        assertThat(listDevicesView.devices.first().setHeatFlowTemperatureZone1).isEqualTo(setHeatFlowTemperatureZone1)
        assertThat(listDevicesView.devices.first().setCoolFlowTemperatureZone1).isEqualTo(setCoolFlowTemperatureZone1)
        assertThat(listDevicesView.devices.first().heatPumpFrequency).isEqualTo(heatPumpFrequency)
        assertThat(listDevicesView.devices.first().outdoorTemperature).isEqualTo(outdoorTemperature)
    }

    @Test
    fun shouldReturnEmptyListDevicesView_whenMelcloudHttpClientReturnsEmptyArrayOfBuildings() {
        every { melcloudHttpClient.listDevices(any()) } returns emptyArray()
        assertThat(mqttService.listDevices().devices).isEmpty()
    }

    @Test
    fun shouldRetrieveMtsContextKey_fromSessionService_whenListDevices() {
        val expectedMtxContextKey = UUID.randomUUID().toString()
        every { sessionService.getSessionKey() } returns expectedMtxContextKey
        mqttService.listDevices()
        verify { melcloudHttpClient.listDevices(expectedMtxContextKey) }
    }

    @Test
    fun shouldRetrieveMtsContextKey_fromSessionService_whenSetHeatFlowTemperatureZone1() {
        val expectedMtxContextKey = UUID.randomUUID().toString()
        every { sessionService.getSessionKey() } returns expectedMtxContextKey
        mqttService.setHeatFlowTemperatureZone1("", "")
        verify { melcloudHttpClient.setHeatFlowTemperatureZone1(expectedMtxContextKey, any(), any()) }
    }
}
