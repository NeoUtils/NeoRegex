package com.neo.regex.model

data class Update(
    val hasUpdate : Boolean? = null,
    val lastVersionCode : Int? = null,
    val lastVersionName : String? = null,
    val downloadLink : String? = null
)