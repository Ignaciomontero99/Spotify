package com.nachomontero.spotify.mainModule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.Cancion
import com.nachomontero.spotify.api.Podcast
import com.nachomontero.spotify.databinding.ItemSongBinding
import com.nachomontero.spotify.mainModule.listener.OnClickListener


class SongAdapter(
    //private val listener: OnClickListener
) : ListAdapter<Cancion, RecyclerView.ViewHolder>(CancionDiffCallBack()) {
    private lateinit var mContext: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemSongBinding.bind(view)

//        fun setListener(cancion: Cancion) {
//            binding.root.setOnClickListener {
//                listener.onClickSong(cancion.id)
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_song, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cancion = getItem(position)

        with(holder as ViewHolder) {
            // setListener(cancion)
            binding.songTitle.text = cancion.titulo
            binding.songArtist.text = cancion.album.artista.nombre

            Glide.with(mContext)
                .load(cancion.album.imagen)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.albumImage)
        }
    }

    class CancionDiffCallBack : DiffUtil.ItemCallback<Cancion>() {
        override fun areItemsTheSame(oldItem: Cancion, newItem: Cancion): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cancion, newItem: Cancion): Boolean {
            return oldItem == newItem
        }
    }
}