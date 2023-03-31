package test.melcloud2mqtt.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import melcloud2mqtt.request.SetAtwRequest
import melcloud2mqtt.service.Melcloud2MqttService
import melcloud2mqtt.service.MelcloudService
import melcloud2mqtt.view.ListDevicesView
import melcloud2mqtt.view.SetAtwView
import mqtt.Publisher
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class Melcloud2MqttServiceTest {

    @InjectMockKs
    lateinit var melcloud2MqttService: Melcloud2MqttService

    @RelaxedMockK
    lateinit var melcloudService: MelcloudService

    @RelaxedMockK
    lateinit var publisher: Publisher

    @Test
    fun shouldNotPublish_givenResponseTopicNull() {
        melcloud2MqttService.listDevices(null)
        melcloud2MqttService.setHeatFlowTemperatureZone1(null, mockk(relaxed = true))
        verify(exactly = 0) { publisher.publish(any(), any(), any(), any()) }
    }

    @Test
    fun shouldPublishResponseFromMelcloudService_whenConsumeListDevices() {
        val expectedResponse = mockk<ListDevicesView>()
        every { melcloudService.listDevices() } returns expectedResponse
        melcloud2MqttService.listDevices("")
        verify { publisher.publish(eq(false), any(), any(), eq(expectedResponse)) }
    }

    @Test
    fun shouldPublishResponseFromMelcloudService_whenSetHeatFlowTemperatureZone1() {
        val expectedResponse = mockk<SetAtwView>()
        every { melcloudService.setHeatFlowTemperatureZone1(any(), any()) } returns expectedResponse
        melcloud2MqttService.setHeatFlowTemperatureZone1("response/topic", mockk(relaxed = true))
        verify { publisher.publish(eq(false), any(), any(), eq(expectedResponse)) }
    }

    @Test
    fun shouldUseDeviceIdAndValueFromPayload_whenSetHeatFlowTemperatureZone1() {
        val deviceId = UUID.randomUUID().toString()
        val value = UUID.randomUUID().toString()
        melcloud2MqttService.setHeatFlowTemperatureZone1(null, SetAtwRequest(deviceId, value))
        verify { melcloudService.setHeatFlowTemperatureZone1(deviceId, value) }
    }
}
