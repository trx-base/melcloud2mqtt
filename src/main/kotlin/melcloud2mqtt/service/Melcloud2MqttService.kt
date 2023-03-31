package melcloud2mqtt.service

import jakarta.inject.Inject
import jakarta.inject.Singleton
import melcloud2mqtt.request.SetAtwRequest
import mqtt.Publisher

@Singleton
class Melcloud2MqttService {

    @Inject
    lateinit var publisher: Publisher

    @Inject
    lateinit var melcloudService: MelcloudService

    fun listDevices(responseTopic: String?) {
        val listDevicesView = melcloudService.listDevices()
        if (responseTopic != null) {
            publisher.publish(false, responseTopic, null, listDevicesView)
        }
    }

    fun setHeatFlowTemperatureZone1(responseTopic: String?, payload: SetAtwRequest) {
        val setAtwView = melcloudService.setHeatFlowTemperatureZone1(payload.deviceId, payload.value)
        if (responseTopic != null) {
            publisher.publish(false, responseTopic, null, setAtwView)
        }
    }
}
