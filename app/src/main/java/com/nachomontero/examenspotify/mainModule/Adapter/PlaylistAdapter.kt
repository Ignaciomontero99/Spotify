package com.nachomontero.examenspotify.mainModule.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nachomontero.examenspotify.databinding.ItemPlaylistBinding
import com.nachomontero.examenspotify.retrofit.data.Playlist

class PlaylistAdapter(
    private var playlists: List<Playlist>,
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {
    inner class PlaylistViewHolder(private val binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: Playlist) {
            binding.playlistTitle.text = playlist.titulo
            binding.root.setOnClickListener { onClick(playlist) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }
    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) = holder.bind(playlists[position])
    override fun getItemCount() = playlists.size

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }
}