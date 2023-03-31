package melcloud2mqtt.client.model

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class DeviceSpecific(
    val DeviceType: String?,
    val FlowTemperature: String?,
    val ReturnTemperature: String?,
    val CurrentEnergyConsumed: String?,
    val CurrentEnergyProduced: String?,
    val DailyHeatingEnergyConsumed: String?,
    val DailyHeatingEnergyProduced: String?,
    val DailyCoolingEnergyConsumed: String?,
    val DailyCoolingEnergyProduced: String?,
    val DailyHotWaterEnergyConsumed: String?,
    val DailyHotWaterEnergyProduced: String?,
    val SetHeatFlowTemperatureZone1: String?,
    val SetCoolFlowTemperatureZone1: String?,
    val HeatPumpFrequency: String?,
    val OutdoorTemperature: String?,
)
