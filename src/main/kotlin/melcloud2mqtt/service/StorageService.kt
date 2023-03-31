package melcloud2mqtt.service

import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class StorageService {

    private val log: Logger = LoggerFactory.getLogger(StorageService::class.java)

    val engine = HashMap<String, String>()

    fun get(key: String): String? {
        log.info("get() - key: $key - value: ${engine[key]}")
        return engine[key]
    }

    fun put(key: String, value: String) {
        log.info("put() - key: $key - value: $value")
        engine[key] = value
    }

    fun remove(key: String) {
        log.info("remove() - key: $key")
        engine.remove(key)
    }
}
