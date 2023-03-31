package integration

import delay.mqtt.MqttClient
import jakarta.inject.Inject
import mu.KLogging
import org.eclipse.paho.mqttv5.common.MqttMessage
import java.util.concurrent.CompletableFuture

abstract class AbstractIntegrationTest {

    private companion object : KLogging()

    @Inject
    lateinit var mqttClient: MqttClient

    fun expectMessage(topic: String): CompletableFuture<MqttMessage> {
        val messageArrived = CompletableFuture<MqttMessage>()
        mqttClient.subscribe(
            topic,
            0,
        ) { msgTopic, message ->
            logger.info("IntegrationTest message arrived. topic: $msgTopic, message: $message")
            messageArrived.complete(message)
        }
        return messageArrived
    }
}
