package com.nachomontero.spotify.api.service

import com.nachomontero.spotify.api.Artista
import com.nachomontero.spotify.api.common.Constants
import retrofit2.http.GET

interface ArtistaService {
    @GET(Constants.PATH_ARTISTS)
    suspend fun getArtistas(): List<Artista>
}