package melcloud2mqtt.client.model

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class DeviceGeneral(val DeviceID: String, val Device: DeviceSpecific)
