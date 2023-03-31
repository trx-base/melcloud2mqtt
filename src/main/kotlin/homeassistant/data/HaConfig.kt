package homeassistant.data

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class HaConfig(
    val device: HaDevice,
    val name: String,
    @JsonProperty("device_class") val deviceClass: String,
    @JsonProperty("state_class") val stateClass: String,
    @JsonProperty("unit_of_measurement") val unitOfMeasurement: Any,
    @JsonProperty("unique_id") val uniqueId: String,
    @JsonProperty("state_topic") val stateTopic: String,
    @JsonProperty("enabled_by_default") val enabledByDefault: Boolean,
    @JsonProperty("value_template") val valueTemplate: String,
)
