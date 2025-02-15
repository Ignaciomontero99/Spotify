package com.nachomontero.examenspotify.retrofit.service

import com.nachomontero.examenspotify.retrofit.data.Cancion
import com.nachomontero.examenspotify.retrofit.data.Playlist
import com.nachomontero.examenspotify.retrofit.data.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SpotifyAPI {
    @GET("usuario/1/playlists")
    suspend fun getUserPlaylists(): List<Playlist>

    @POST("playlist")
    @FormUrlEncoded
    suspend fun createPlaylist(
        @Field("titulo") titulo: String,
        @Field("numero_canciones") numCanciones: Int,
        @Field("usuario_id") usuarioId: Int
    ): Response<Void>

    @PUT("usuario/1/playlist/{id}")
    suspend fun updatePlaylist(
        @Path("id") id: Int,
        @Body playlist: Playlist
    ): Response<Void>

    @GET("playlist/{id}/canciones")
    suspend fun getSongsFromPlaylist(
        @Path("id") playlistId: Int
    ): List<Cancion>

    @GET("usuario/{id}")
    suspend fun getUser(
        @Path("id") id: Int
    ): Usuario

    @PUT("usuario/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body user: Usuario
    ): Response<Void>

    @DELETE("playlist/{id}")
    suspend fun deletePlaylist(
        @Path("id") id: Int
    ): Response<Void>

}