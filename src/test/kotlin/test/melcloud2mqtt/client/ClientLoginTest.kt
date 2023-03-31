package test.melcloud2mqtt.client

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import io.micronaut.http.HttpMethod
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
import melcloud2mqtt.client.exception.ClientLoginException
import melcloud2mqtt.client.model.ClientLogin
import melcloud2mqtt.client.model.LoginData
import melcloud2mqtt.client.request.ClientLoginRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ClientLoginTest {

    @InjectMockKs
    lateinit var melcloudHttpClient: MelcloudHttpClient

    @RelaxedMockK
    lateinit var httpClient: HttpClient

    @RelaxedMockK
    lateinit var blockingHttpClient: BlockingHttpClient

    var httpRequestSlot = slot<HttpRequest<ClientLoginRequest>>()

    @BeforeEach
    fun setUp() {
        every { httpClient.toBlocking() } returns blockingHttpClient
        every { blockingHttpClient.retrieve(capture(httpRequestSlot), ClientLogin::class.java) } returns mockk(
            relaxed = true,
        )
    }

    @Test
    fun shouldUseRetrieve_withBlockingHttpClient_whenListDevices() {
        melcloudHttpClient.clientLogin("", "")
        verify { blockingHttpClient.retrieve(any<HttpRequest<Any>>(), any<Class<Any>>()) }
    }

    @Test
    fun shouldSetEmail_inBody_whenGivenAsParameter() {
        val expectedEmail = UUID.randomUUID().toString()
        melcloudHttpClient.clientLogin(expectedEmail, "")

        assertThat(httpRequestSlot.captured.body.get().email).isEqualTo(expectedEmail)
    }

    @Test
    fun shouldSetPassword_inBody_whenGivenAsParameter() {
        val expectedPassword = UUID.randomUUID().toString()
        melcloudHttpClient.clientLogin("", expectedPassword)

        assertThat(httpRequestSlot.captured.body.get().password).isEqualTo(expectedPassword)
    }

    @Test
    fun shouldSetLanguageToZero_inBody() {
        melcloudHttpClient.clientLogin("", "")
        assertThat(httpRequestSlot.captured.body.get().language).isEqualTo("0")
    }

    @Test
    fun shouldSetAppVersionToStaticValue_inBody() {
        melcloudHttpClient.clientLogin("", "")
        assertThat(httpRequestSlot.captured.body.get().appVersion).isEqualTo("1.25.2.0")
    }

    @Test
    fun shouldSetPersistToTrue_inBody() {
        melcloudHttpClient.clientLogin("", "")
        assertThat(httpRequestSlot.captured.body.get().persist).isTrue()
    }

    @Test
    fun shouldReturnLoginData_whenGivenInClientLoginResponse() {
        val expectedLoginData: LoginData = mockk(relaxed = true)
        every {
            blockingHttpClient.retrieve(
                capture(httpRequestSlot),
                ClientLogin::class.java,
            )
        } returns ClientLogin("", expectedLoginData)

        val response = melcloudHttpClient.clientLogin("", "")
        assertThat(response).isEqualTo(expectedLoginData)
    }

    @Test
    fun shouldThrow_whenErrorIdInResponse_Is_1() {
        every {
            blockingHttpClient.retrieve(
                capture(httpRequestSlot),
                ClientLogin::class.java,
            )
        } returns ClientLogin("1", null)

        assertThrows<ClientLoginException> { melcloudHttpClient.clientLogin("", "") }
    }

    @Test
    fun shouldSetRequestUrlPath_whenGivenHttpRequest() {
        melcloudHttpClient.clientLogin("", "")
        assertThat(httpRequestSlot.captured.path).isEqualTo("/Mitsubishi.Wifi.Client/Login/ClientLogin")
    }

    @Test
    fun shouldUsePost_whenGivenHttpRequest() {
        melcloudHttpClient.clientLogin("", "")
        assertThat(httpRequestSlot.captured.method).isEqualTo(HttpMethod.POST)
    }

    @Test
    fun shouldMapContextKey_whenGivenInResponse() {
        val expectedLoginData = LoginData("ExpectedContextKey")

        every {
            blockingHttpClient.retrieve(
                capture(httpRequestSlot),
                ClientLogin::class.java,
            )
        } returns ClientLogin("", expectedLoginData)

        val response = melcloudHttpClient.clientLogin("", "")
        assertThat(response.ContextKey).isEqualTo("ExpectedContextKey")
    }
}
