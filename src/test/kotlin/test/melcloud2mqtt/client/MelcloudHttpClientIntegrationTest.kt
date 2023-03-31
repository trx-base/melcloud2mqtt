package test.melcloud2mqtt.client

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import melcloud2mqtt.client.MelcloudHttpClient
import melcloud2mqtt.client.exception.ClientLoginException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest
class MelcloudHttpClientIntegrationTest {

    @Inject
    lateinit var melcloudHttpClient: MelcloudHttpClient

    @Test
    fun shouldReceiveResponse_whenListDevices() {
        val response = melcloudHttpClient.listDevices("8FC9B49B4B8VALIDCONTEXTKEY")
        assertThat(response).isNotNull()
    }

    @Test
    fun shouldThrowWithStatusUnauthorized_givenInvalidMitsContextKey_whenListDevices() {
        val ex = assertThrows<HttpClientResponseException> {
            melcloudHttpClient.listDevices("8FC9B49B4B8THISMUSTNOTEXIST")
        }
        assertThat(ex.status).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun shouldMapSingleBuilding_whenListDevices() {
        val response = melcloudHttpClient.listDevices("8FC9B49B4B8VALIDCONTEXTKEY")
        assertThat(response).hasSize(1)

        val building = response.first()
        assertThat(building.Structure).isNotNull()
        assertThat(building.Structure.Devices).hasSize(1)

        val generalDevice = building.Structure.Devices.first()
        assertThat(generalDevice.DeviceID).isEqualTo("1919191919")
        assertThat(generalDevice.Device).isNotNull()

        val specificDevice = generalDevice.Device
        assertThat(specificDevice.DeviceType).isEqualTo("1")
        assertThat(specificDevice.FlowTemperature).isEqualTo("45.5")
        assertThat(specificDevice.ReturnTemperature).isEqualTo("37.0")
        assertThat(specificDevice.DailyHeatingEnergyConsumed).isEqualTo("51.08")
        assertThat(specificDevice.DailyHeatingEnergyProduced).isEqualTo("120.3")
        assertThat(specificDevice.DailyCoolingEnergyConsumed).isEqualTo("0.0")
        assertThat(specificDevice.DailyCoolingEnergyProduced).isEqualTo("0.0")
        assertThat(specificDevice.DailyHotWaterEnergyConsumed).isEqualTo("0.0")
        assertThat(specificDevice.DailyHotWaterEnergyProduced).isEqualTo("0.0")
        assertThat(specificDevice.CurrentEnergyConsumed).isEqualTo("3")
        assertThat(specificDevice.CurrentEnergyProduced).isEqualTo("6")
        assertThat(specificDevice.SetHeatFlowTemperatureZone1).isEqualTo("46.0")
        assertThat(specificDevice.SetCoolFlowTemperatureZone1).isEqualTo("0.0")
        assertThat(specificDevice.HeatPumpFrequency).isEqualTo("58")
    }

    @Test
    fun shouldMapLoginData_whenClientLogin() {
        val loginData = melcloudHttpClient.clientLogin("validaccount@melcloud.com", "ValidPassword")
        assertThat(loginData.ContextKey).isEqualTo("8FC9B49B4B8VALIDCONTEXTKEY")
    }

    @Test
    fun shouldFail_givenInvalidCredentials_whenClientLogin() {
        assertThrows<ClientLoginException> {
            melcloudHttpClient.clientLogin(
                "mustnotexist@melcloud.com",
                "MustNotExist",
            )
        }
    }

    @Test
    fun shouldReturnSameValueWhen_ListDevices_calledAfter_SetHeatFlowTemperatureZone1Operation() {
        val expectedTemperature = "46.0"

        val clientLogin = melcloudHttpClient.clientLogin("validaccount@melcloud.com", "ValidPassword")
        val listDevices1 = melcloudHttpClient.listDevices(clientLogin.ContextKey)
        val setAtw = melcloudHttpClient.setHeatFlowTemperatureZone1(
            clientLogin.ContextKey,
            listDevices1.first().Structure.Devices.first().DeviceID,
            expectedTemperature,
        )
        val listDevices2 = melcloudHttpClient.listDevices(clientLogin.ContextKey)

        assertThat(setAtw.SetHeatFlowTemperatureZone1).isEqualTo(expectedTemperature)
        assertThat(listDevices2.first().Structure.Devices.first().Device.SetHeatFlowTemperatureZone1).isEqualTo(
            expectedTemperature,
        )
    }

    @Test
    fun shouldReturnSpecificDeviceWithNullValues_givenSpecificDeviceWithNullValues_whenListDevices() {
        val listDevices = melcloudHttpClient.listDevices("8FC9B49B4B8EMPTYSPECIFICDEVICE")
        assertThat(listDevices.first().Structure.Devices.first().Device.SetHeatFlowTemperatureZone1).isNull()
    }

    @Test
    fun shouldReturnSetAtwWithNullValues_givenSetAtwWithNullValues_whenSetHeatflowTemperatureZone1() {
        val setAtw = melcloudHttpClient.setHeatFlowTemperatureZone1(" ", "8FC9B49B4B8EMPTYSETATW", " ")
        assertThat(setAtw.SetHeatFlowTemperatureZone1).isNull()
    }

    @Test
    fun shouldMapLoginData_whenClientSavedLogin() {
        val loginData = melcloudHttpClient.clientSavedLogin("8FC9B49B4B8VALIDCONTEXTKEY")
        assertThat(loginData.ContextKey).isEqualTo("8FC9B49B4B8VALIDCONTEXTKEY")
    }

    @Test
    fun shouldFail_givenInvalidCredentials_whenClientSavedLogin() {
        assertThrows<ClientLoginException> {
            melcloudHttpClient.clientSavedLogin("8FC9B49B4B8THISMUSTNOTEXIST)")
        }
    }
}
