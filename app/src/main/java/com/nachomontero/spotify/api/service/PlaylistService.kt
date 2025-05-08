package com.nachomontero.spotify.api.service

import com.nachomontero.spotify.api.Playlist
import com.nachomontero.spotify.api.common.Constants
import retrofit2.http.GET
import retrofit2.http.Path

interface PlaylistService {
    @GET(Constants.PATH_PLAYLIST_FROM_USER)
    suspend fun getPlaylist(@Path("userId") userId: Int): List<Playlist>
}