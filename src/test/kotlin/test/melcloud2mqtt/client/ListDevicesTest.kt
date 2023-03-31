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
import io.mockk.verify
import melcloud2mqtt.client.MelcloudHttpClient
import melcloud2mqtt.client.model.Building
import melcloud2mqtt.client.model.DeviceGeneral
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ListDevicesTest {

    @InjectMockKs
    lateinit var melcloudHttpClient: MelcloudHttpClient

    @RelaxedMockK
    lateinit var httpClient: HttpClient

    @RelaxedMockK
    lateinit var blockingHttpClient: BlockingHttpClient

    @BeforeEach
    fun setUp() {
        every { httpClient.toBlocking() } returns blockingHttpClient
        every {
            blockingHttpClient.retrieve(
                any<HttpRequest<Any>>(),
                any<Class<Any>>(),
            )
        } returns arrayOf<Building>()
    }

    @Test
    fun shouldUseRetrieve_withBlockingHttpClient() {
        melcloudHttpClient.listDevices("")
        verify { blockingHttpClient.retrieve(any<HttpRequest<Any>>(), any<Class<Array<DeviceGeneral>>>()) }
    }

    @Test
    fun shouldSetMitsContextKey_inHeader_whenGivenAsParameter() {
        val httpRequestSlot = slot<HttpRequest<Any>>()
        every { blockingHttpClient.retrieve(capture(httpRequestSlot), Array<Building>::class.java) } returns arrayOf()
        val expectedMitsContextKey = UUID.randomUUID().toString()
        melcloudHttpClient.listDevices(expectedMitsContextKey)
        assertThat(httpRequestSlot.captured.headers.get("X-MitsContextKey")).isEqualTo(expectedMitsContextKey)
    }

    @Test
    fun shouldSetRequestUrlPath_whenRetrieveFromHttpClient() {
        val httpRequestSlot = slot<HttpRequest<Any>>()
        every {
            blockingHttpClient.retrieve(
                capture(httpRequestSlot),
                any<Class<Any>>(),
            )
        } returns arrayOf<Building>()

        melcloudHttpClient.listDevices("")
        assertThat(httpRequestSlot.captured.path).isEqualTo("/Mitsubishi.Wifi.Client/User/ListDevices")
    }

    @Test
    fun shouldReturnBuildingInResponse_whenGivenByHttpClient() {
        val expectedBuilding: Building = mockk(relaxed = true)
        every {
            blockingHttpClient.retrieve(
                any<HttpRequest<Any>>(),
                any<Class<Any>>(),
            )
        } returns arrayOf(expectedBuilding)

        val response = melcloudHttpClient.listDevices("")
        assertThat(response.first()).isEqualTo(expectedBuilding)
    }
}
