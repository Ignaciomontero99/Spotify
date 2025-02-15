package com.nachomontero.examenspotify.mainModule.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nachomontero.examenspotify.retrofit.Common.Constants
import com.nachomontero.examenspotify.retrofit.data.Playlist
import kotlinx.coroutines.launch

class PlaylistViewModel : ViewModel() {
    val playlists = MutableLiveData<List<Playlist>>()

    fun fetchPlaylists() {
        viewModelScope.launch { playlists.value = Constants.api.getUserPlaylists() }
    }
}