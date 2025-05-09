package com.nachomontero.spotify.songModule.DetailModule

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.Cancion
import com.nachomontero.spotify.api.Playlist
import com.nachomontero.spotify.api.common.Constants
import com.nachomontero.spotify.api.service.CancionService
import com.nachomontero.spotify.api.service.PlaylistService
import com.nachomontero.spotify.databinding.ActivitySongDetailBinding
import com.nachomontero.spotify.mainModule.adapter.PlaylistAdapter
import com.nachomontero.spotify.mainModule.adapter.SongAdapter
import com.nachomontero.spotify.mainModule.listener.OnClickListener
import com.nachomontero.spotify.sharedPreferences.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.text.get

class SongDetailActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySongDetailBinding

    private lateinit var playlistList: List<Playlist>
    private lateinit var playlistRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mBinding = ActivitySongDetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val titulo = intent.getStringExtra("titulo")
        val duracion = intent.getStringExtra("duracion")
        val songId = intent.getIntExtra("cancionId", -1)

        if (songId != -1) {
            getSongById(songId) // Implementa esta función
        } else {
            Toast.makeText(this, "No se encontró la canción", Toast.LENGTH_SHORT).show()
            finish()
        }


        mBinding.songTitle.text = titulo
        mBinding.songDuration.text = "Duración: $duracion"

        playlistRecyclerView = findViewById(R.id.playlistRecyclerView)
        playlistRecyclerView.layoutManager = GridLayoutManager(this, 2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mBinding.btnAddToPlaylist.setOnClickListener {
            if (::playlistList.isInitialized && playlistList.isNotEmpty()) {
                showPlaylistDialog()
            } else {
                Toast.makeText(this, "No hay playlists disponibles", Toast.LENGTH_SHORT).show()
            }
        }
        window.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        loadPlaylists()

    }

    private fun getSongById(songId: Int) {
        val retrofit = provideRetrofit()
        val service = retrofit.create(CancionService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val cancion = service.getCancionInfo(songId)
                withContext(Dispatchers.Main) {

                    mBinding.songTitle.text = cancion.titulo
                    mBinding.songDuration.text = cancion.duracion
                }
            } catch (e: Exception) {
                Log.e("SongDetail", "Error cargando canción: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SongDetailActivity, "Error al cargar la canción", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun provideRetrofit(): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    private fun loadPlaylists() {
        val userId = SessionManager.getUserId(this)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PlaylistService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = service.getPlaylist(userId)
                withContext(Dispatchers.Main) {
                    playlistList = response
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SongDetailActivity, "Error al cargar playlists", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showPlaylistDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_playlist_selector, null)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerPlaylists)

        val playlistTitles = playlistList.map { it.titulo }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, playlistTitles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        AlertDialog.Builder(this)
            .setTitle("Selecciona una playlist")
            .setView(dialogView)
            .setPositiveButton("Añadir") { _, _ ->
                val selectedIndex = spinner.selectedItemPosition
                val selectedPlaylist = playlistList[selectedIndex]
                addSongToPlaylist(selectedPlaylist.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun addSongToPlaylist(playlistId: Int) {
        val songId = intent.getIntExtra("cancionId", -1)
        val userId = SessionManager.getUserId(this)

        if (songId == -1) {
            Toast.makeText(this, "Canción no válida", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val playlistService = retrofit.create(PlaylistService::class.java)
        val cancionService = retrofit.create(CancionService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val canciones = cancionService.getCancionesFromPlaylist(playlistId)

                val alreadyInPlaylist = canciones.any { it.id == songId }

                if (alreadyInPlaylist) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@SongDetailActivity,
                            "La canción ya está en la playlist",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    playlistService.addSongToPlaylist(playlistId = playlistId, cancionId = songId, usuario_id = userId)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@SongDetailActivity,
                            "Canción añadida exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SongDetailActivity,
                        "Error al añadir la canción",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}


