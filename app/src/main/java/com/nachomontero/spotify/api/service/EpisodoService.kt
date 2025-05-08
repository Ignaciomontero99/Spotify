package com.nachomontero.spotify.api.service

import com.nachomontero.spotify.api.Episodio
import com.nachomontero.spotify.api.common.Constants
import retrofit2.http.GET
import retrofit2.http.Path

interface EpisodoService {
    @GET(Constants.PATH_EPISODE_FROM_PODCAST)
    suspend fun getEpisodiosFromPodcast(@Path("podcastId") podcastId: Int): List<Episodio>

}