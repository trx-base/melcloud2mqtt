package test.melcloud2mqtt.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import melcloud2mqtt.client.exception.ClientLoginException
import melcloud2mqtt.client.model.LoginData
import melcloud2mqtt.config.MelcloudConfig
import melcloud2mqtt.service.SessionService
import melcloud2mqtt.service.StorageService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class SessionServiceTest {

    @InjectMockKs
    lateinit var sessionService: SessionService

    @RelaxedMockK
    lateinit var melcloudConfig: MelcloudConfig

    @RelaxedMockK
    lateinit var melcloudHttpClient: melcloud2mqtt.client.MelcloudHttpClient

    @RelaxedMockK
    lateinit var storageService: StorageService

    @Test
    fun shouldRetrieveContextKeyFromClient_withGivenCredentialsInConfig_whenGetSessionKey() {
        val email = UUID.randomUUID().toString()
        val password = UUID.randomUUID().toString()
        val mitsContextKey = UUID.randomUUID().toString()

        every { melcloudConfig.email } returns email
        every { melcloudConfig.password } returns password
        every { melcloudHttpClient.clientLogin(email, password) } returns LoginData(mitsContextKey)

        assertThat(sessionService.getSessionKey()).isEqualTo(mitsContextKey)
    }

    @Test
    fun shouldReturnKeyFromStorage_whenGivenInStorage() {
        val expected = UUID.randomUUID().toString()
        every { storageService.get("MitsContextKey") } returns expected
        assertThat(sessionService.getSessionKey()).isEqualTo(expected)
    }

    @Test
    fun shouldPersistKeyInStorage_whenGivenByHttpClient() {
        val expected = UUID.randomUUID().toString()
        every { melcloudHttpClient.clientLogin(any(), any()) } returns LoginData(expected)
        sessionService.getSessionKey()
        verify { storageService.put("MitsContextKey", expected) }
    }

    @Test
    fun shouldLoginAgain_whenSavedLogin_ThrowsClientLoginException() {
        every { storageService.get("MitsContextKey") } returns "KeyInStorage"
        every { melcloudHttpClient.clientLogin(any(), any()) } returns LoginData("KeyFromLogin")
        every { melcloudHttpClient.clientSavedLogin("KeyInStorage") } throws ClientLoginException()
        assertThat(sessionService.getSessionKey()).isEqualTo("KeyFromLogin")
    }
}
