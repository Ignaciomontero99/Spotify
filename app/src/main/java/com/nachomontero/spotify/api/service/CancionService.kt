package com.nachomontero.spotify.api.service

import com.nachomontero.spotify.api.Cancion
import com.nachomontero.spotify.api.common.Constants
import retrofit2.http.GET

interface CancionService {
    @GET(Constants.PATH_SONGS)
    suspend fun getCanciones(): List<Cancion>
}