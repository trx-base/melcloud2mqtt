package test.melcloud2mqtt.service

import delay.mqtt.MqttClient
import delay.mqtt.MqttConfig
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import melcloud2mqtt.service.SubscriptionService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class SubscriptionServiceTest {

    @InjectMockKs
    var subscriptionService = SubscriptionService()

    @MockK(relaxed = true)
    lateinit var mqttClient: MqttClient

    @MockK
    lateinit var mqttConfig: MqttConfig

    @BeforeEach
    fun setUp() {
        every { mqttConfig.topic } returns "delayed-SubscriptionServiceTest"
    }

    @Test
    fun shouldSubscribeToTopics_whenRegisterSubscriptions_andGivenClientId() {
        subscriptionService.registerSubscriptions()
        val topic = mqttConfig.topic
        verify { mqttClient.subscribe("$topic/listDevices", 2, any()) }
        verify { mqttClient.subscribe("+/$topic/listDevices", 2, any()) }

        verify { mqttClient.subscribe("$topic/setHeatFlowTemperatureZone1", 2, any()) }
        verify { mqttClient.subscribe("+/$topic/setHeatFlowTemperatureZone1", 2, any()) }
    }
}
