package homeassistant.data

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class HaDevice(val identifiers: List<String>, val name: String)
