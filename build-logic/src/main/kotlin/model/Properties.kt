package model

import java.io.File

data class Properties(
    val storeFile: File,
    val storePassword: String,
    val keyAlias: String,
    val keyPassword: String
)