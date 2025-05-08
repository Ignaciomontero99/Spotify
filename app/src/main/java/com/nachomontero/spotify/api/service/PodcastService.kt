package com.nachomontero.spotify.api.service

import com.nachomontero.spotify.api.Podcast
import com.nachomontero.spotify.api.common.Constants
import retrofit2.http.GET
import retrofit2.http.Path

interface PodcastService {
    @GET(Constants.PATH_PODCAST_FROM_USER)
    suspend fun getPodcastFromUser(@Path("userId") userId: Int): List<Podcast>

    @GET(Constants.PATH_PODCASTS)
    suspend fun getPodcast(): List<Podcast>


}