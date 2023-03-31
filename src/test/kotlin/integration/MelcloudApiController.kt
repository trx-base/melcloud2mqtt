package integration

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import melcloud2mqtt.client.request.ClientLoginRequest
import melcloud2mqtt.client.request.SetHeatFlowTemperatureZone1Request
import melcloud2mqtt.service.StorageService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller
class MelcloudApiController {

    @Get("/")
    fun index(): HttpResponse<String> {
        return HttpResponse.ok<String>().body("Simulator not implemented yet.")
    }

    @Post("/Mitsubishi.Wifi.Client/Login/ClientLogin")
    fun clientLogin(@Body clientLoginRequest: ClientLoginRequest): HttpResponse<String> {
        val staticJson =
            if (clientLoginRequest.email == "validaccount@melcloud.com" && clientLoginRequest.password == "ValidPassword") {
                this::class.java.classLoader.getResource("mock-responses/ClientLogin-success.json")!!.readText()
            } else {
                this::class.java.classLoader.getResource("mock-responses/ClientLogin-failure.json")!!.readText()
            }
        return HttpResponse.ok<String>().body(staticJson)
    }

    @Get("/Mitsubishi.Wifi.Client/Login/ClientSavedLogin")
    fun clientSavedLogin(key: String): HttpResponse<String> {
        val staticJson =
            if (key == "8FC9B49B4B8VALIDCONTEXTKEY") {
                this::class.java.classLoader.getResource("mock-responses/ClientLogin-success.json")!!.readText()
            } else {
                this::class.java.classLoader.getResource("mock-responses/ClientLogin-failure.json")!!.readText()
            }
        return HttpResponse.ok<String>().body(staticJson)
    }

    @Get("/Mitsubishi.Wifi.Client/User/ListDevices")
    fun listDevices(@Header("X-MitsContextKey") mitsContextKey: String): HttpResponse<String> {
        when (mitsContextKey) {
            "8FC9B49B4B8VALIDCONTEXTKEY" -> {
                return HttpResponse.ok<String>().body(
                    this::class.java.classLoader.getResource("mock-responses/ListDevices.json")!!.readText(),
                )
            }

            "8FC9B49B4B8EMPTYSPECIFICDEVICE" -> {
                return HttpResponse.ok<String>().body(
                    this::class.java.classLoader.getResource("mock-responses/ListDevices-emptySpecificDevice.json")!!
                        .readText(),
                )
            }
        }
        return HttpResponse.unauthorized()
    }

    @Post("/Mitsubishi.Wifi.Client/Device/SetAtw")
    fun setAtw(@Body request: SetHeatFlowTemperatureZone1Request): HttpResponse<String> {
        when (request.deviceId) {
            "8FC9B49B4B8EMPTYSETATW" -> {
                return HttpResponse.ok<String>()
                    .body(this::class.java.classLoader.getResource("mock-responses/SetAtw-empty.json")!!.readText())
            }
        }
        return HttpResponse.ok<String>()
            .body(this::class.java.classLoader.getResource("mock-responses/SetAtw.json")!!.readText())
    }
}
