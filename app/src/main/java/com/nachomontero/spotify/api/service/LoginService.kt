package com.nachomontero.spotify.api.service

import com.nachomontero.spotify.api.Usuario
import com.nachomontero.spotify.api.common.Constants
import retrofit2.http.GET
import retrofit2.http.Path

interface LoginService {
    @GET(Constants.PATH_USERS)
    suspend fun getUser(): Usuario
}