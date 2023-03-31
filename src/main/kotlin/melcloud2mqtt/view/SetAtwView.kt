package melcloud2mqtt.view

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class SetAtwView(val setHeatFlowTemperatureZone1: String?)
