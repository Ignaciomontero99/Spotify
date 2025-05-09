package com.nachomontero.spotify.mainModule.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.Cancion
import com.nachomontero.spotify.api.common.Constants
import com.nachomontero.spotify.api.service.CancionService
import com.nachomontero.spotify.api.service.PlaylistService
import com.nachomontero.spotify.databinding.ItemSongBinding
import com.nachomontero.spotify.mainModule.listener.OnClickListener
import com.nachomontero.spotify.songModule.SongListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SongAdapter(
    private val listener: OnClickListener,
    private val playlistId: Int? = null,
    private val onSongDeleted: ((Int) -> Unit)? = null
) : ListAdapter<Cancion, RecyclerView.ViewHolder>(CancionDiffCallBack()) {
    private lateinit var mContext: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemSongBinding.bind(view)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PlaylistService::class.java)

        fun setListener(cancion: Cancion) {
            binding.root.setOnClickListener {
                listener.onClickSong(cancion.id)
            }
            binding.root.setOnLongClickListener {
                if (playlistId != null) {
                    AlertDialog.Builder(binding.root.context)
                        .setTitle("Eliminar canción")
                        .setMessage("¿Seguro que quieres eliminar esta canción de la playlist?")
                        .setPositiveButton("Sí") { _, _ ->
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    service.deleteSongFromPlaylist(
                                        playlistId = playlistId,
                                        cancionId = cancion.id
                                    )
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            binding.root.context,
                                            "Canción eliminada",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onSongDeleted?.invoke(cancion.id)
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            binding.root.context,
                                            "Error al eliminar",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
                true
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_song, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cancion = getItem(position)

        with(holder as ViewHolder) {
            setListener(cancion)
            binding.songTitle.text = cancion.titulo
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
