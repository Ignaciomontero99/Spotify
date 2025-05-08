package com.nachomontero.spotify.mainModule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.Episodio
import com.nachomontero.spotify.databinding.ItemEpisodeBinding
import com.nachomontero.spotify.databinding.ItemSongBinding

class EpisodeAdapter(
    //private val listener: OnClickListener
) : ListAdapter<Episodio, RecyclerView.ViewHolder>(EpisodeDiffCallBack()) {
    private lateinit var mContext: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemEpisodeBinding.bind(view)

//        fun setListener(episodio: Episodio) {
//            binding.root.setOnClickListener {
//                listener.onClickEpisodio(episodio.id)
//            }
//        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_episode, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val episode = getItem(position)

        with (holder as ViewHolder) {
            //setListener(episodie)
            binding.episodeTitle.text = episode.titulo
            binding.episodeDescript.text = episode.descripcion
        }
    }

    class EpisodeDiffCallBack : DiffUtil.ItemCallback<Episodio>() {
        override fun areItemsTheSame(oldItem: Episodio, newItem: Episodio): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Episodio, newItem: Episodio): Boolean {
            return oldItem == newItem
        }
    }
}