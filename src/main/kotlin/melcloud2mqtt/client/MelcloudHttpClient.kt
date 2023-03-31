package melcloud2mqtt.client

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import jakarta.inject.Singleton
import melcloud2mqtt.client.exception.ClientLoginException
import melcloud2mqtt.client.model.Building
import melcloud2mqtt.client.model.ClientLogin
import melcloud2mqtt.client.model.LoginData
import melcloud2mqtt.client.model.SetAtw
import melcloud2mqtt.client.request.ClientLoginRequest
import melcloud2mqtt.client.request.SetHeatFlowTemperatureZone1Request

@Singleton
class MelcloudHttpClient(@param:Client("melcloud-api") var httpClient: HttpClient) {

    fun clientLogin(email: String, password: String): LoginData {
        val request = HttpRequest.POST(
            "/Mitsubishi.Wifi.Client/Login/ClientLogin",
            ClientLoginRequest(email, password, "0", "1.25.2.0", true),
        )
        val response = httpClient.toBlocking().retrieve(request, ClientLogin::class.java)

        if (response.ErrorId == "1") {
            throw ClientLoginException()
        }
        return response.LoginData!!
    }

    fun clientSavedLogin(mtsContextKey: String): LoginData {
        val request = HttpRequest.GET<Any>(
            "/Mitsubishi.Wifi.Client/Login/ClientSavedLogin/?key=$mtsContextKey&appVersion=1.25.2.0",
        )
        val response = httpClient.toBlocking().retrieve(request, ClientLogin::class.java)

        if (response.ErrorId == "1") {
            throw ClientLoginException()
        }
        return response.LoginData!!
    }

    fun listDevices(mitsContextKey: String): Array<Building> {
        val request = HttpRequest.GET<Any>("/Mitsubishi.Wifi.Client/User/ListDevices")
        request.headers.set("X-MitsContextKey", mitsContextKey)
        return httpClient.toBlocking().retrieve(request, Array<Building>::class.java)
    }

    fun setHeatFlowTemperatureZone1(mitsContextKey: String, deviceId: String, value: String): SetAtw {
        val request = HttpRequest.POST(
            "/Mitsubishi.Wifi.Client/Device/SetAtw",
            SetHeatFlowTemperatureZone1Request(281474976710656, deviceId, value),
        )
        request.headers.set("X-MitsContextKey", mitsContextKey)
        return httpClient.toBlocking().retrieve(request, SetAtw::class.java)
    }
}
