package com.nachomontero.spotify.api.service

import com.nachomontero.spotify.api.Album
import com.nachomontero.spotify.api.common.Constants
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumesService {
    @GET(Constants.PATH_ALBUMS_FROM_USER)
    suspend fun getAlbumes(@Path("userId") userId: Int): List<Album>
}