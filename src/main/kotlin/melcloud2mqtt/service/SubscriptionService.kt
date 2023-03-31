package melcloud2mqtt.service

import delay.mqtt.MqttClient
import delay.mqtt.MqttConfig
import io.micronaut.context.annotation.Context
import io.micronaut.serde.ObjectMapper
import jakarta.inject.Inject
import melcloud2mqtt.request.SetAtwRequest
import mu.KLogging
import org.eclipse.paho.mqttv5.common.MqttMessage
import javax.annotation.PostConstruct

@Context
class SubscriptionService {

    private companion object : KLogging()

    @Inject
    lateinit var mqttClient: MqttClient

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var melcloud2MqttService: Melcloud2MqttService

    @Inject
    lateinit var mqttConfig: MqttConfig

    @PostConstruct
    fun registerSubscriptions() {
        logger.info { "Registering subscriptions." }
        mqttClient.subscribe("${mqttConfig.topic}/listDevices", 2, this::listDevices)
        mqttClient.subscribe("+/${mqttConfig.topic}/listDevices", 2, this::listDevices)

        mqttClient.subscribe("${mqttConfig.topic}/setHeatFlowTemperatureZone1", 2, this::setHeatFlowTemperatureZone1)
        mqttClient.subscribe("+/${mqttConfig.topic}/setHeatFlowTemperatureZone1", 2, this::setHeatFlowTemperatureZone1)
    }

    fun listDevices(topic: String, mqttMessage: MqttMessage) {
        logger.info("listDevices message received. topic: $topic, mqttMessage: $mqttMessage")
        melcloud2MqttService.listDevices(mqttMessage.properties.responseTopic)
    }

    fun setHeatFlowTemperatureZone1(topic: String, mqttMessage: MqttMessage) {
        logger.info("setHeatFlowTemperatureZone1 message received. topic: $topic, mqttMessage: $mqttMessage")
        val payload = objectMapper.readValue(mqttMessage.payload, SetAtwRequest::class.java)!!
        melcloud2MqttService.setHeatFlowTemperatureZone1(mqttMessage.properties.responseTopic, payload)
    }
}
