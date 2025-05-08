package com.nachomontero.spotify.mainModule.listener

import com.nachomontero.spotify.api.Cancion

interface OnClickListener {
    fun onClickPlaylist(id: Int)
    fun onClickAlbum(id: Int)
    fun onClickPodcast(id: Int)
    fun onClickSong(id: Int)

    fun onSongAddedToPlaylist(cancion: Cancion)
}