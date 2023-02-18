import java.nio.file.Files
import java.nio.file.Path
import java.util.*

private val properties = Properties().apply {
    Files.newInputStream(Path.of("config.properties")).use { load(it) }
}

val TG_TOKEN = properties["TG_TOKEN"] as String

const val NOTIFICATION_DELAY = 120
