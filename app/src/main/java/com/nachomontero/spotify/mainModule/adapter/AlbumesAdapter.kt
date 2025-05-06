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
import com.nachomontero.spotify.api.Album
import com.nachomontero.spotify.databinding.ItemAlbumBinding
import com.nachomontero.spotify.mainModule.MainActivity
import com.nachomontero.spotify.mainModule.listener.OnClickListener

class AlbumesAdapter(
    private val listener: OnClickListener,
    private val isClickable: Boolean = true
) : ListAdapter<Album, RecyclerView.ViewHolder>(AlbumDiffCallBack()) {
        private lateinit var mContext: Context

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val binding = ItemAlbumBinding.bind(view)

            fun setListener(album: Album) {
                if (isClickable) {
                    binding.root.setOnClickListener {
                        listener.onClickAlbum(album.id)
                    }
                } else {
                    binding.root.setOnClickListener(null)
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            mContext = parent.context

            val view = LayoutInflater.from(mContext).inflate(R.layout.item_album, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val album = getItem(position)

            with(holder as ViewHolder) {
                setListener(album)

                binding.tvTitulo.text = album.titulo
                binding.tvArtista.text = album.artista.nombre

                Glide.with(mContext)
                    .load(album.imagen)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(binding.ivAlbumImage)
            }
        }

        class AlbumDiffCallBack : DiffUtil.ItemCallback<Album>() {
            override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
                return oldItem == newItem
            }
        }
    }