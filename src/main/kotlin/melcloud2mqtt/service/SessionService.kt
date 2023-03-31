package melcloud2mqtt.service

import jakarta.inject.Inject
import jakarta.inject.Singleton
import melcloud2mqtt.client.exception.ClientLoginException
import melcloud2mqtt.config.MelcloudConfig

@Singleton
class SessionService {

    @Inject
    lateinit var melcloudConfig: MelcloudConfig

    @Inject
    lateinit var melcloudHttpClient: melcloud2mqtt.client.MelcloudHttpClient

    @Inject
    lateinit var storageService: StorageService

    fun getSessionKey(): String {
        var sessionKey = storageService.get("MitsContextKey")
        if (!isKeyValid(sessionKey)) {
            sessionKey = melcloudHttpClient.clientLogin(melcloudConfig.email, melcloudConfig.password).ContextKey
            storageService.put("MitsContextKey", sessionKey)
        }
        return sessionKey!!
    }

    private fun isKeyValid(sessionKey: String?): Boolean {
        if (sessionKey.isNullOrBlank()) {
            return false
        }
        try {
            melcloudHttpClient.clientSavedLogin(sessionKey)
        } catch (ex: ClientLoginException) {
            return false
        }
        return true
    }
}
