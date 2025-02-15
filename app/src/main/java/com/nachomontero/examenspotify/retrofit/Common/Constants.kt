package com.nachomontero.examenspotify.retrofit.Common

import com.nachomontero.examenspotify.retrofit.service.SpotifyAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Constants {
    const val BASE_URL = "http://musica.navelsystems.com"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://musica.navelsystems.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: SpotifyAPI by lazy { retrofit.create(SpotifyAPI::class.java) }
}