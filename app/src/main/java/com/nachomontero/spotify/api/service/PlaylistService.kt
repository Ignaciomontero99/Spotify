package com.nachomontero.spotify.api.service

import com.nachomontero.spotify.api.Playlist
import com.nachomontero.spotify.api.PlaylistResponse
import com.nachomontero.spotify.api.common.Constants
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaylistService {
    @GET(Constants.PATH_PLAYLIST_FROM_USER)
    suspend fun getPlaylist(@Path("userId") userId: Int): List<Playlist>

    @FormUrlEncoded
    @POST(Constants.PATH_PLAYLIST_FROM_USER)
    suspend fun postPlaylist(
        @Path("userId") userId: Int,
        @Field("titulo") titulo: String,
        @Field("numero_canciones") numero_canciones: Int,
        @Field("usuario_id") usuario_id: Int): PlaylistResponse


    @POST(Constants.PATH_SONGS_INTO_PLAYLIST)
    suspend fun addSongToPlaylist(
        @Path("playlistId") playlistId: Int,
        @Path("cancionId") cancionId: Int,
        @Query("usuario_id") usuario_id: Int
    )

    @DELETE(Constants.PATH_DELETE_SONG_FROM_PLAYLIST)
    suspend fun deleteSongFromPlaylist(
        @Path("playlistId") playlistId: Int,
        @Path("cancionId") cancionId: Int
    )


}