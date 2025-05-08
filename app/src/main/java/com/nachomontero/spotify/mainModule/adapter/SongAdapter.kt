package com.nachomontero.spotify.mainModule.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.Cancion
import com.nachomontero.spotify.api.service.PlaylistService
import com.nachomontero.spotify.databinding.ItemSongBinding
import com.nachomontero.spotify.mainModule.listener.OnClickListener
import com.nachomontero.spotify.sharedPreferences.SessionManager
import com.nachomontero.spotify.songModule.SongListActivity
import kotlinx.coroutines.launch


class SongAdapter(
    private val listener: OnClickListener,
    private var playlistService: PlaylistService
) : ListAdapter<Cancion, RecyclerView.ViewHolder>(CancionDiffCallBack()) {

    private lateinit var mContext: Context

    var onSongLongClickListener: ((Cancion) -> Unit)? = null

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemSongBinding.bind(view)

        fun setListener(cancion: Cancion) {
            binding.root.setOnClickListener {
                listener.onClickSong(cancion.id)
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

            Glide.with(mContext)
                .load(cancion.album?.imagen)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.albumImage)

            // Manejo del clic largo (Long Click) para eliminar la canción localmente
            holder.itemView.setOnLongClickListener {
                onSongLongClickListener?.invoke(cancion)
                true
            }

            // Clic en el item para mostrar el BottomSheet
            binding.root.setOnClickListener {
                val bottomSheetView =
                    LayoutInflater.from(mContext).inflate(R.layout.activity_song_detail, null)
                val dialog = BottomSheetDialog(mContext)
                dialog.setContentView(bottomSheetView)

                val titleView = bottomSheetView.findViewById<TextView>(R.id.songTitle)
                val durationView = bottomSheetView.findViewById<TextView>(R.id.songDuration)
                val btnAdd = bottomSheetView.findViewById<Button>(R.id.btnAddToPlaylist)
                val albumImage = bottomSheetView.findViewById<ImageView>(R.id.albumCover)
                val playlistRecyclerView =
                    bottomSheetView.findViewById<RecyclerView>(R.id.playlistRecyclerView)

                titleView.text = cancion.titulo
                durationView.text = "Duración: ${cancion.duracion}"

                Glide.with(mContext)
                    .load(cancion.album?.imagen)
                    .placeholder(R.drawable.ic_music)
                    .into(albumImage)

                // Obtener el lifecycleScope del contexto (Activity o Fragment)
                if (mContext is AppCompatActivity) {
                    val activity = mContext as AppCompatActivity
                    activity.lifecycleScope.launch {
                        try {
                            val playlists = playlistService.getPlaylist(1) // Cambiar el ID según sea necesario
                            val playlistAdapter = PlaylistAdapter(object : OnClickListener {
                                override fun onClickPlaylist(playlistId: Int) {
                                    val selectedPlaylist = playlists.find { it.id == playlistId }
                                    selectedPlaylist?.let {
                                        Toast.makeText(
                                            mContext,
                                            "Añadida '${cancion.titulo}' a la playlist ${it.titulo}",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Agregar la canción a la lista local
                                        SessionManager.agregarCancionLocal(mContext, cancion)

                                        // Actualizar el RecyclerView con las canciones locales
                                        (this@SongAdapter.mContext as? SongListActivity)?.actualizarRecyclerViewConCancionesLocales(this@SongAdapter.mContext)

                                        // Actualizamos el adaptador con la nueva lista de canciones locales
                                        val cancionesLocales = SessionManager.obtenerCancionesLocalesDesdePreferences(mContext)
                                        submitList(cancionesLocales)

                                        dialog.dismiss()
                                    }
                                }

                                override fun onClickAlbum(id: Int) {
                                    TODO("Not yet implemented")
                                }

                                override fun onClickPodcast(id: Int) {
                                    TODO("Not yet implemented")
                                }

                                override fun onClickSong(id: Int) {
                                    TODO("Not yet implemented")
                                }

                                override fun onSongAddedToPlaylist(cancion: Cancion) {
                                    TODO("Not yet implemented")
                                }
                            })

                            playlistRecyclerView.layoutManager = LinearLayoutManager(mContext)
                            playlistRecyclerView.adapter = playlistAdapter
                            playlistAdapter.submitList(playlists)

                        } catch (e: Exception) {
                            Toast.makeText(
                                mContext,
                                "Error al cargar las playlists",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("Cancion", e.message.toString())
                        }
                    }
                }
                dialog.show()
            }

        }
    }

    // Método que devuelve un DiffUtil para comparar canciones
    class CancionDiffCallBack : DiffUtil.ItemCallback<Cancion>() {
        override fun areItemsTheSame(oldItem: Cancion, newItem: Cancion): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cancion, newItem: Cancion): Boolean {
            return oldItem == newItem
        }
    }
}
