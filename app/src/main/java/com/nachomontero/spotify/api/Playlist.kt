package com.nachomontero.spotify.api

data class Playlist(
    val id: Int,
    val titulo: String,
    val fechaCreacion: String,
    val usuario: Usuario
)