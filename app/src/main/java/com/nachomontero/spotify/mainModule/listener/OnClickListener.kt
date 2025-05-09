package com.nachomontero.spotify.mainModule.listener

interface OnClickListener {
    fun onClickPlaylist(id: Int)
    fun onClickAlbum(id: Int)
    fun onClickPodcast(id: Int)
    fun onClickSong(id: Int)
    fun onClickEpisode(id: Int)
}