package melcloud2mqtt.service

import jakarta.inject.Inject
import jakarta.inject.Singleton
import melcloud2mqtt.client.MelcloudHttpClient
import melcloud2mqtt.client.model.DeviceGeneral
import melcloud2mqtt.view.DeviceView
import melcloud2mqtt.view.ListDevicesView
import melcloud2mqtt.view.SetAtwView

@Singleton
class MelcloudService {

    @Inject
    lateinit var melcloudHttpClient: MelcloudHttpClient

    @Inject
    lateinit var sessionService: SessionService

    fun setHeatFlowTemperatureZone1(deviceId: String, value: String): SetAtwView {
        val mitsContextKey = sessionService.getSessionKey()
        val setAtw = melcloudHttpClient.setHeatFlowTemperatureZone1(mitsContextKey, deviceId, value)
        return SetAtwView(setAtw.SetHeatFlowTemperatureZone1)
    }

    fun listDevices(): ListDevicesView {
        val mitsContextKey = sessionService.getSessionKey()
        val buildings = melcloudHttpClient.listDevices(mitsContextKey)

        val deviceViews = mutableListOf<DeviceView>()
        if (buildings.isNotEmpty()) {
            buildings[0].Structure.Devices.map(::mapDeviceGeneralToDeviceView).toCollection(deviceViews)
        }

        return ListDevicesView(deviceViews)
    }

    private fun mapDeviceGeneralToDeviceView(deviceGeneral: DeviceGeneral): DeviceView {
        return DeviceView(
            deviceGeneral.DeviceID,
            deviceGeneral.DeviceName,
            deviceGeneral.Device.DeviceType,
            deviceGeneral.Device.FlowTemperature,
            deviceGeneral.Device.ReturnTemperature,
            deviceGeneral.Device.CurrentEnergyConsumed,
            deviceGeneral.Device.CurrentEnergyProduced,
            deviceGeneral.Device.DailyHeatingEnergyConsumed,
            deviceGeneral.Device.DailyHeatingEnergyProduced,
            deviceGeneral.Device.DailyCoolingEnergyConsumed,
            deviceGeneral.Device.DailyCoolingEnergyProduced,
            deviceGeneral.Device.DailyHotWaterEnergyConsumed,
            deviceGeneral.Device.DailyHotWaterEnergyProduced,
            deviceGeneral.Device.SetHeatFlowTemperatureZone1,
            deviceGeneral.Device.SetCoolFlowTemperatureZone1,
            deviceGeneral.Device.HeatPumpFrequency,
            deviceGeneral.Device.OutdoorTemperature,
        )
    }
}
