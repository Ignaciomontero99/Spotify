package com.nachomontero.spotify.api.service

import com.nachomontero.spotify.api.Usuario
import com.nachomontero.spotify.api.common.Constants
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST(Constants.PATH_USERS)
    suspend fun login(@Body usuario: Usuario): Usuario
}