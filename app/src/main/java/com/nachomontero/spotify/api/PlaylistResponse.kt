package com.nachomontero.spotify.api

import com.google.gson.annotations.SerializedName

data class PlaylistResponse(
    val id: Int,
    val titulo: String,
    val fechaCreacion: String? = null,
    @SerializedName("usuario")
    val usuarioId: Int
)
