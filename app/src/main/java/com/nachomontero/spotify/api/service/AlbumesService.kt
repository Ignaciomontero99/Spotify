package com.nachomontero.spotify.api.service

import com.nachomontero.spotify.api.Album
import com.nachomontero.spotify.api.common.Constants
import retrofit2.http.GET

interface AlbumesService {
    @GET(Constants.PATH_ALBUMS)
    suspend fun getAlbumes(): List<Album>
}