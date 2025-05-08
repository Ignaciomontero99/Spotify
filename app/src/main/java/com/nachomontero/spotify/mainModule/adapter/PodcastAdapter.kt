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
import com.nachomontero.spotify.api.Podcast
import com.nachomontero.spotify.databinding.ItemPodcastBinding
import com.nachomontero.spotify.mainModule.listener.OnClickListener

class PodcastAdapter(
    private val listener: OnClickListener
) : ListAdapter<Podcast, RecyclerView.ViewHolder>(PodcastDiffCallBack()) {
    private lateinit var mContext: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemPodcastBinding.bind(view)

        fun setListener(podcast: Podcast) {
            binding.root.setOnClickListener {
                listener.onClickPodcast(podcast.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_podcast, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val podcast = getItem(position)

        with(holder as ViewHolder) {
            setListener(podcast)

            binding.tvTitulo.text = podcast.titulo

            Glide.with(mContext)
                .load(podcast.imagen)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.ivPodcastCover)

        }
    }

    class PodcastDiffCallBack : DiffUtil.ItemCallback<Podcast>() {
        override fun areItemsTheSame(oldItem: Podcast, newItem: Podcast): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Podcast, newItem: Podcast): Boolean {
            return oldItem == newItem
        }
    }
}