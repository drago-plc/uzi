package com.lomolo.uzi.model

data class SignIn(
    val firstName: String,
    val lastName: String,
    val phone: String
) {
    constructor(): this("", "", "")
}