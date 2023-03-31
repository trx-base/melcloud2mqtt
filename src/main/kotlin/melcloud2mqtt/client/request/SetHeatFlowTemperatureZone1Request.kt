package melcloud2mqtt.client.request

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class SetHeatFlowTemperatureZone1Request(
    val effectiveFlags: Long,
    val deviceId: String,
    val setHeatFlowTemperatureZone1: String,
)
