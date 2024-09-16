package extension

import model.Properties
import java.io.File

fun File.properties(): Properties? {

    if (!exists()) return null

    val properties = java.util.Properties().apply {
        load(inputStream())
    }

    return Properties(
        storeFile = File(parent, properties.getProperty("STORE_FILE")),
        storePassword = properties.getProperty("STORE_PASSWORD"),
        keyAlias = properties.getProperty("KEY_ALIAS"),
        keyPassword = properties.getProperty("KEY_PASSWORD")
    )
}