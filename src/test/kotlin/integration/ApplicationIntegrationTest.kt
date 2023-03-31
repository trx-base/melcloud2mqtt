package integration

import assertk.assertThat
import assertk.assertions.isEqualTo
import delay.mqtt.MqttConfig
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.serde.ObjectMapper
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import melcloud2mqtt.request.SetAtwRequest
import melcloud2mqtt.view.ListDevicesView
import melcloud2mqtt.view.SetAtwView
import mqtt.Publisher
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.concurrent.TimeUnit

@MicronautTest
class ApplicationIntegrationTest : AbstractIntegrationTest() {

    @Inject
    lateinit var application: EmbeddedApplication<*>

    @Inject
    lateinit var publisher: Publisher

    @Inject
    lateinit var mqttConfig: MqttConfig

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Test
    fun testItWorks() {
        Assertions.assertTrue(application.isRunning)
    }

    @Test
    fun shouldReceiveResponseMessage_whenListDevices() {
        val expectedMessage = expectMessage("${mqttConfig.topic}/response/listDevices")

        publisher.publish(
            false,
            "${mqttConfig.topic}/listDevices",
            "${mqttConfig.topic}/response/listDevices",
            null,
        )
        val messageReceived = expectedMessage.get(3, TimeUnit.SECONDS)
        val expectedListDevicesView = objectMapper.readValue(messageReceived.payload, ListDevicesView::class.java)!!
        assertThat(expectedListDevicesView.devices.first().type).isEqualTo("1")
    }

    @ParameterizedTest
    @ValueSource(strings = ["46.0"])
    fun shouldReceiveResponseMessage_whenSetHeatFlowTemperatureZone1(value: String) {
        val expectedMessage = expectMessage("${mqttConfig.topic}/response/setHeatFlowTemperatureZone1")
        publisher.publish(
            false,
            "${mqttConfig.topic}/setHeatFlowTemperatureZone1",
            "${mqttConfig.topic}/response/setHeatFlowTemperatureZone1",
            SetAtwRequest("55958112", value),
        )
        val messageReceived = expectedMessage.get(3, TimeUnit.SECONDS)
        val setAtwView = objectMapper.readValue(messageReceived.payload, SetAtwView::class.java)!!

        assertThat(setAtwView.setHeatFlowTemperatureZone1).isEqualTo(value)
    }

    @ParameterizedTest
    @ValueSource(strings = ["46.0"])
    fun shouldHaveSetTemperatureWhenListDevices_givenPreviouslySetTemperature(value: String) {
        publisher.publish(
            false,
            "${mqttConfig.topic}/setHeatFlowTemperatureZone1",
            "${mqttConfig.topic}/response/setHeatFlowTemperatureZone1",
            SetAtwRequest("55958112", value),
        )

        val expectedMessage = expectMessage("${mqttConfig.topic}/response/listDevices")
        publisher.publish(
            false,
            "${mqttConfig.topic}/listDevices",
            "${mqttConfig.topic}/response/listDevices",
            null,
        )
        val messageReceived = expectedMessage.get(3, TimeUnit.SECONDS)
        val expectedListDevicesView = objectMapper.readValue(messageReceived.payload, ListDevicesView::class.java)!!
        assertThat(expectedListDevicesView.devices.first().setHeatFlowTemperatureZone1).isEqualTo(value)
    }
}
