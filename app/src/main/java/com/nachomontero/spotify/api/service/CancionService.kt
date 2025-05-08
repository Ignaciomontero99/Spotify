package com.nachomontero.spotify.api.service

import com.nachomontero.spotify.api.Cancion
import com.nachomontero.spotify.api.common.Constants
import retrofit2.http.GET
import retrofit2.http.Path

interface CancionService {
    @GET(Constants.PATH_SONGS)
    suspend fun getCanciones(): List<Cancion>

    @GET(Constants.PATH_SONGS_FROM_PLAYLIST)
    suspend fun getCancionesFromPlaylist(@Path("playlistId") playlistId: Int): List<Cancion>

    @GET(Constants.PATH_SONGS_FROM_ALBUMS)
    suspend fun getCancionesFromAlbum(@Path("albumId") albumId: Int): List<Cancion>

}