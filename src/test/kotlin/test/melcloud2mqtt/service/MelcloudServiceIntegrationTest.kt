package test.melcloud2mqtt.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.junit5.MockKExtension
import jakarta.inject.Inject
import melcloud2mqtt.service.MelcloudService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@MicronautTest
@ExtendWith(MockKExtension::class)
class MelcloudServiceIntegrationTest {

    @Inject
    lateinit var service: MelcloudService

    @Test
    fun shouldPublishPayload_whenSetHeatFlowTemperatureZone1() {
        val payload = service.setHeatFlowTemperatureZone1("test/melcloud/response/setAtw", "device1")
        assertThat(payload.setHeatFlowTemperatureZone1).isEqualTo("46.0")
    }

    @Test
    fun shouldPublishPayload_whenListDevices() {
        val payload = service.listDevices()
        assertThat(payload.devices.first().type).isEqualTo("1")
    }
}
