package com.nachomontero.spotify.api.service

import com.nachomontero.spotify.api.PlaylistWrapper
import com.nachomontero.spotify.api.common.Constants
import retrofit2.http.GET

interface PlaylistService {
    @GET(Constants.PATH_PLAYLIST)
    suspend fun getPlaylist(): List<List<PlaylistWrapper>>
}