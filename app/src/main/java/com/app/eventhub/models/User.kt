package com.app.eventhub.models

import com.beust.klaxon.*

private val klaxon = Klaxon()

data class User (
    val id: String? = null,
    val name: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val identification: String? = null,
    var image: String? = null
) {


    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = klaxon.parse<User>(json)
    }
}