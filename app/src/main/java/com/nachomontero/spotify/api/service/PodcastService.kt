package com.nachomontero.spotify.api.service

import com.nachomontero.spotify.api.Podcast
import com.nachomontero.spotify.api.common.Constants
import retrofit2.http.GET

interface PodcastService {
    @GET(Constants.PATH_PODCAST)
    suspend fun getPodcast(): List<Podcast>
}