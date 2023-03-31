package melcloud2mqtt.view

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class DeviceView(
    @JsonProperty("Id") val id: String,
    @JsonProperty("Name") val name: String,
    @JsonProperty("Type") val type: String?,
    @JsonProperty("FlowTemperature") val flowTemperature: String?,
    @JsonProperty("ReturnTemperature") val returnTemperature: String?,
    @JsonProperty("CurrentEnergyConsumed") val currentEnergyConsumed: String?,
    @JsonProperty("CurrentEnergyProduced") val currentEnergyProduced: String?,
    @JsonProperty("DailyHeatingEnergyConsumed") val dailyHeatingEnergyConsumed: String?,
    @JsonProperty("DailyHeatingEnergyProduced") val dailyHeatingEnergyProduced: String?,
    @JsonProperty("DailyCoolingEnergyConsumed") val dailyCoolingEnergyConsumed: String?,
    @JsonProperty("DailyCoolingEnergyProduced") val dailyCoolingEnergyProduced: String?,
    @JsonProperty("DailyHotWaterEnergyConsumed") val dailyHotWaterEnergyConsumed: String?,
    @JsonProperty("DailyHotWaterEnergyProduced") val dailyHotWaterEnergyProduced: String?,
    @JsonProperty("SetHeatFlowTemperatureZone1") val setHeatFlowTemperatureZone1: String?,
    @JsonProperty("SetCoolFlowTemperatureZone1") val setCoolFlowTemperatureZone1: String?,
    @JsonProperty("HeatPumpFrequency") val heatPumpFrequency: String?,
    @JsonProperty("OutdoorTemperature") val outdoorTemperature: String?,
)
