package com.nachomontero.spotify.api

data class Usuario (
    val id: String? = null,
    val username: String,
    val password: String,
    val email: String? = null,
    val genero: String? = null,
    val fechaNacimiento: String? = null,
    val pais: String? = null,
    val codigoPostal: String? = null
)