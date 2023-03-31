package melcloud2mqtt.client.model

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class Structure(val Devices: Array<DeviceGeneral>)
