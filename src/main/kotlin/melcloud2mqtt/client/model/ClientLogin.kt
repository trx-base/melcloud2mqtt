package melcloud2mqtt.client.model

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class ClientLogin(val ErrorId: String?, val LoginData: LoginData?)
