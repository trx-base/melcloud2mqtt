package homeassistant

import homeassistant.data.HaConfig
import homeassistant.data.HaDevice
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Inject
import jakarta.inject.Singleton
import melcloud2mqtt.service.MelcloudService
import mqtt.Publisher

@Singleton
open class HomeAssistantService {

    @Inject
    lateinit var publisher: Publisher

    @Inject
    lateinit var melcloudService: MelcloudService

    @Inject
    lateinit var homeAssistantConfig: HomeAssistantConfig

    @Scheduled(initialDelay = "10s")
    fun config() {
        val deviceView = melcloudService.listDevices().devices.first()
        val deviceId = "melcloud2mqtt_" + deviceView.id
        val deviceName = "MELCloud " + deviceView.name

        publishTemperatureSensor(deviceId, deviceName, "FlowTemperature")
        publishTemperatureSensor(deviceId, deviceName, "ReturnTemperature")
        publishTemperatureSensor(deviceId, deviceName, "SetHeatFlowTemperatureZone1")
        publishTemperatureSensor(deviceId, deviceName, "OutdoorTemperature")
    }

    private fun publishTemperatureSensor(deviceId: String, deviceName: String, sensorName: String) {
        val topic = homeAssistantConfig.discoveryTopic
        publisher.publish(
            true,
            "$topic/sensor/$deviceId/$sensorName/config",
            null,
            HaConfig(
                device = HaDevice(arrayListOf(deviceId), deviceName),
                sensorName,
                "temperature",
                "measurement",
                "°C",
                "${deviceId}_$sensorName",
                "$topic/sensor/$deviceId/state",
                true,
                "{{ value_json.$sensorName}}",
            ),
        )
    }

    fun deleteConfig() {
        val deviceView = melcloudService.listDevices().devices.first()
        val deviceId = "melcloud2mqtt_" + deviceView.id
        val topic = homeAssistantConfig.discoveryTopic
        publisher.publish(
            true,
            "$topic/sensor/$deviceId/FlowTemperature/config",
            null,
            null,
        )
    }

    @Scheduled(initialDelay = "20s", fixedDelay = "5m")
    fun updateState() {
        val deviceView = melcloudService.listDevices().devices.first()
        val deviceId = "melcloud2mqtt_" + deviceView.id
        val topic = homeAssistantConfig.discoveryTopic
        publisher.publish(false, "$topic/sensor/$deviceId/state", null, deviceView)
    }
}
