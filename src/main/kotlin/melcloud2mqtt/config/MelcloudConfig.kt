package melcloud2mqtt.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("melcloud")
class MelcloudConfig {
    lateinit var email: String
    lateinit var password: String
}
