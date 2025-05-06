package com.nachomontero.spotify.mainModule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.Album
import com.nachomontero.spotify.api.Playlist
import com.nachomontero.spotify.databinding.ItemPlaylistBinding
import com.nachomontero.spotify.mainModule.MainActivity
import com.nachomontero.spotify.mainModule.listener.OnClickListener

class PlaylistAdapter(
    private val listener: OnClickListener,
    private val isClickable: Boolean = true
) : ListAdapter<Playlist, RecyclerView.ViewHolder>(PlaylistDiffCallBack()) {
    private lateinit var mContext: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemPlaylistBinding.bind(view)

        fun setListener(playlist: Playlist) {
            if (isClickable) {
                binding.root.setOnClickListener {
                    listener.onClickPlaylist(playlist.id)
                }
            } else {
                binding.root.setOnClickListener(null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_playlist, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val playlist = getItem(position)

        with(holder as ViewHolder) {
            setListener(playlist)

            binding.tvTitulo.text = playlist.titulo
        }
    }

    class PlaylistDiffCallBack : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem == newItem
        }
    }
}