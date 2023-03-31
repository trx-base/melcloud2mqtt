package test.melcloud2mqtt.client

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import melcloud2mqtt.client.MelcloudHttpClient
import melcloud2mqtt.client.model.SetAtw
import melcloud2mqtt.client.request.SetHeatFlowTemperatureZone1Request
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class SetHeatFlowTemperatureZone1Test {

    @InjectMockKs
    lateinit var melcloudHttpClient: MelcloudHttpClient

    @RelaxedMockK
    lateinit var httpClient: HttpClient

    @RelaxedMockK
    lateinit var blockingHttpClient: BlockingHttpClient

    var httpRequestSlot = slot<HttpRequest<SetHeatFlowTemperatureZone1Request>>()

    @BeforeEach
    fun setUp() {
        every { httpClient.toBlocking() } returns blockingHttpClient
        every {
            blockingHttpClient.retrieve(
                capture(httpRequestSlot),
                SetAtw::class.java,
            )
        } returns mockk(relaxed = true)
    }

    @Test
    fun shouldReturnResponse_givenInMelcloudApiResponse_isDifferent() {
        every {
            blockingHttpClient.retrieve(
                capture(httpRequestSlot),
                SetAtw::class.java,
            )
        } returns SetAtw("99_unexpected")
        assertThat(
            melcloudHttpClient.setHeatFlowTemperatureZone1(
                "",
                "",
                "42",
            ).SetHeatFlowTemperatureZone1,
        ).isEqualTo("99_unexpected")
    }

    @Test
    fun shouldSetRequestUrlPath_givenHttpRequest() {
        melcloudHttpClient.setHeatFlowTemperatureZone1("", "", "")
        assertThat(httpRequestSlot.captured.path).isEqualTo("/Mitsubishi.Wifi.Client/Device/SetAtw")
    }

    @Test
    fun shouldHaveTemperatureInRequest_givenAsParameter() {
        melcloudHttpClient.setHeatFlowTemperatureZone1("", "", "1878")
        assertThat(httpRequestSlot.captured.body.get().setHeatFlowTemperatureZone1).isEqualTo("1878")
    }

    @Test
    fun shouldSetMitsContextKey_inHeader_givenAsParameter() {
        val expectedMitsContextKey = UUID.randomUUID().toString()
        melcloudHttpClient.setHeatFlowTemperatureZone1(expectedMitsContextKey, "", "")
        assertThat(httpRequestSlot.captured.headers.get("X-MitsContextKey")).isEqualTo(expectedMitsContextKey)
    }

    @Test
    fun shouldSetDeviceID_inBody_givenAsParameter() {
        val expectedDeviceID = UUID.randomUUID().toString()
        melcloudHttpClient.setHeatFlowTemperatureZone1("", expectedDeviceID, "")
        assertThat(httpRequestSlot.captured.body.get().deviceId).isEqualTo(expectedDeviceID)
    }

    @Test
    fun shouldEffectiveFlags_inBody_givenAsStaticValue() {
        melcloudHttpClient.setHeatFlowTemperatureZone1("", "", "")
        assertThat(httpRequestSlot.captured.body.get().effectiveFlags).isEqualTo(281474976710656)
    }
}
