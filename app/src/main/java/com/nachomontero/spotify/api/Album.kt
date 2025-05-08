package com.nachomontero.spotify.api

data class Album(
    val id: Int,
    val titulo: String,
    val anyo: String,
    val imagen: String,
    val fechaInicioPatrocinio: String,
    val fechaFinPatrocinio: String
)
