package melcloud2mqtt.client.request

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class ClientLoginRequest(
    val email: String,
    val password: String,
    val language: String,
    val appVersion: String,
    val persist: Boolean,
)
