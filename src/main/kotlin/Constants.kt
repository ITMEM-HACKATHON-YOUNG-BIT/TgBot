import java.nio.file.Files
import java.nio.file.Path
import java.util.*

private val properties = Properties().apply {
    Files.newInputStream(Path.of("config.properties")).use { load(it) }
}

val BASE_URL = properties["BASE_URL"] as String

val TG_TOKEN = properties["TG_TOKEN"] as String

const val NOTIFICATION_DELAY = 300
