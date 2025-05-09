package com.nachomontero.spotify.api.common

object Constants {
    const val BASE_URL = "https://musica.navelsystems.com"

    const val PATH_USERS = "/usuario/1"
    const val PATH_PLAYLIST_FROM_USER = "/usuario/{userId}/playlists"
    const val PATH_PODCAST_FROM_USER = "/usuario/{userId}/podcasts"
    const val PATH_ALBUMS_FROM_USER ="/usuario/{userId}/albums"

    const val PATH_PODCASTS = "/podcasts"
    const val PATH_SONGS = "/canciones"

    const val PATH_INFO_SONG = "/cancion/{cancionId}"
    const val PATH_INFO_EPISODE_FROM_PODCAST = "/podcast/{podcastId}/capitulo/{capituloId}"

    const val PATH_SONGS_FROM_ALBUMS = "/album/{albumId}/canciones"
    const val PATH_SONGS_FROM_PLAYLIST = "/playlist/{playlistId}/canciones"
    const val PATH_EPISODE_FROM_PODCAST = "/podcast/{podcastId}/capitulos"

    const val PATH_SONGS_INTO_PLAYLIST = "/playlist/{playlistId}/canciones/{cancionId}"

    const val PATH_DELETE_SONG_FROM_PLAYLIST = "/playlist/{playlistId}/canciones/{cancionId}"


}