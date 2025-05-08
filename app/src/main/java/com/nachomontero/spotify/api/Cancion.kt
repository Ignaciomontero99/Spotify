package com.nachomontero.spotify.api

data class Cancion(
    val id: Int,
    val titulo: String,
    val duracion: String,
    val numeroReproducciones: Int,
    val album: Album?
)

