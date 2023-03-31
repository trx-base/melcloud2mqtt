package mqtt

import delay.mqtt.MqttClient
import io.micronaut.serde.ObjectMapper
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.eclipse.paho.mqttv5.common.MqttMessage
import org.eclipse.paho.mqttv5.common.packet.MqttProperties

@Singleton
class Publisher {

    @Inject
    lateinit var mqttClient: MqttClient

    @Inject
    lateinit var objectMapper: ObjectMapper

    fun publish(retained: Boolean, topic: String, responseTopic: String?, payload: Any?) {
        val mqttMessage = MqttMessage()
        if (payload != null) {
            mqttMessage.payload = objectMapper.writeValueAsBytes(payload)
        }
        mqttMessage.isRetained = retained
        mqttMessage.properties = MqttProperties()
        mqttMessage.properties.responseTopic = responseTopic
        mqttClient.publish(topic, mqttMessage)
    }
}

// @MqttPublisher
// interface Publisher {
//    fun publish(
//        @Retained retained: Boolean,
//        @Topic topic: String,
//        @ResponseTopic responseTopic: String?,
//        payload: Any?,
//    ): CompletableFuture<Void>
// }
