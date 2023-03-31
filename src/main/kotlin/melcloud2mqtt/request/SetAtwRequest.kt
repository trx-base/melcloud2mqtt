package melcloud2mqtt.request

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class SetAtwRequest(val deviceId: String, val value: String)
