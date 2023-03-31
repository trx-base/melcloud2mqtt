package homeassistant

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("homeassistant")
class HomeAssistantConfig {
    lateinit var discoveryTopic: String
    lateinit var statusTopic: String
}
