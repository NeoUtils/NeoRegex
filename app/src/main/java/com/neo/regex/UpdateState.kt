package com.neo.regex

data class UpdateState(
    val hasUpdate : Boolean? = null,
    val lastVersionCode : Int? = null,
    val lastVersionName : String? = null,
    val downloadLink : String? = null
)