package melcloud2mqtt.view

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class ListDevicesView(val devices: List<DeviceView>)
