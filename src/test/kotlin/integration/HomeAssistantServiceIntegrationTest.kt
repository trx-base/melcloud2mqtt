package test.homeassistant

import assertk.assertThat
import assertk.assertions.isEqualTo
import homeassistant.HomeAssistantConfig
import homeassistant.HomeAssistantService
import integration.AbstractIntegrationTest
import io.micronaut.serde.ObjectMapper
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import melcloud2mqtt.view.DeviceView
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

@MicronautTest
class HomeAssistantServiceIntegrationTest : AbstractIntegrationTest() {

    @Inject
    lateinit var homeAssistantService: HomeAssistantService

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var homeAssistantConfig: HomeAssistantConfig

    @Test
    fun shouldDoSomething_whenUpdateState() {
        val deviceId = "1919191919"
        val messageArrived = expectMessage("${homeAssistantConfig.discoveryTopic}/sensor/melcloud2mqtt_$deviceId/state")

        homeAssistantService.updateState()

        val expectedDeviceView = DeviceView(
            deviceId,
            "General Device Name",
            "1",
            "45.5",
            "37.0",
            "3",
            "6",
            "51.08",
            "120.3",
            "0.0",
            "0.0",
            "0.0",
            "0.0",
            "46.0",
            "0.0",
            "58",
            "0.0",
        )
        val receivedPayload =
            objectMapper.readValue(messageArrived.get(1, TimeUnit.SECONDS).payload, DeviceView::class.java)
        assertThat(receivedPayload).isEqualTo(expectedDeviceView)
    }
}
