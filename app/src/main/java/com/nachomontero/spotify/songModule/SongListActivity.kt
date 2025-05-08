package com.nachomontero.spotify.songModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.Cancion
import com.nachomontero.spotify.api.common.Constants
import com.nachomontero.spotify.api.service.CancionService
import com.nachomontero.spotify.api.service.PlaylistService
import com.nachomontero.spotify.databinding.ActivitySongListBinding
import com.nachomontero.spotify.libraryModule.LibraryActivity
import com.nachomontero.spotify.mainModule.MainActivity
import com.nachomontero.spotify.mainModule.adapter.SongAdapter
import com.nachomontero.spotify.mainModule.listener.OnClickListener
import com.nachomontero.spotify.sharedPreferences.SessionManager
import com.nachomontero.spotify.songModule.DetailModule.SongDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SongListActivity : AppCompatActivity(), OnClickListener {
    private lateinit var mBinding: ActivitySongListBinding

    private lateinit var songAdapter: SongAdapter
    private lateinit var songLinearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mBinding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val playlistId = intent.getIntExtra("playlistId", -1)
        val albumId = intent.getIntExtra("albumId", -1)

        when {
            albumId != -1 -> {
                setUpRecyclerViewSongAlbum(albumId)
            }
            playlistId != -1 -> {
                setUpRecyclerViewSongPlaylist(playlistId)
            }
            else -> {
                setUpRecyclerViewSong()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupBottomNav()

        // Configurar el listener para el long click en las canciones
        songAdapter.onSongLongClickListener = { cancion ->
            // Eliminar la canción de SharedPreferences
            eliminarCancionLocal(cancion)
        }

        // Actualiza el RecyclerView con las canciones locales al iniciar la actividad
        actualizarRecyclerViewConCancionesLocales(context = this)
    }

    private fun setUpRecyclerViewSongAlbum(albumId: Int) {
        val retrofit = provideRetrofit()
        val playlistService = retrofit.create(PlaylistService::class.java)

        songAdapter = SongAdapter(this, playlistService)
        songLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mBinding.recyclerViewSongs.apply {
            setHasFixedSize(true)
            layoutManager = songLinearLayoutManager
            adapter = songAdapter
        }
        getSongsFromAlbum(albumId)
    }

    private fun getSongsFromAlbum(albumId: Int) {
        val retrofit = provideRetrofit()

        val service = retrofit.create(CancionService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val cancionList = service.getCancionesFromAlbum(albumId)

                withContext(Dispatchers.Main) {
                    if (cancionList.isEmpty()) {
                        Log.d("AlbumList", "Canciones recibidas: ${cancionList.size}")
                        Toast.makeText(this@SongListActivity, "No hay canciones en este álbum", Toast.LENGTH_SHORT).show()
                    } else {
                        songAdapter.submitList(cancionList)
                    }
                }
            } catch (e: Exception) {
                Log.e("Cancion", e.message.toString())
            }
        }
    }

    private fun setUpRecyclerViewSongPlaylist(playlistId: Int) {
        val retrofit = provideRetrofit()
        val playlistService = retrofit.create(PlaylistService::class.java)

        songAdapter = SongAdapter(this, playlistService)
        songLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mBinding.recyclerViewSongs.apply {
            setHasFixedSize(true)
            layoutManager = songLinearLayoutManager
            adapter = songAdapter
        }
        getSongsFromPlaylist(playlistId)
    }

    private fun getSongsFromPlaylist(playlistId: Int) {
        val retrofit = provideRetrofit()

        val service = retrofit.create(CancionService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val cancionList = service.getCancionesFromPlaylist(playlistId)

                withContext(Dispatchers.Main) {
                    if (cancionList.isEmpty()) {
                        Toast.makeText(this@SongListActivity, "No hay canciones en esta playlist", Toast.LENGTH_SHORT).show()
                    } else {
                        songAdapter.submitList(cancionList)
                    }
                }
            } catch (e: Exception) {
                Log.e("Cancion", e.message.toString())
            }
        }
    }

    private fun setUpRecyclerViewSong() {
        val retrofit = provideRetrofit()
        val playlistService = retrofit.create(PlaylistService::class.java)

        songAdapter = SongAdapter(this, playlistService)
        songLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mBinding.recyclerViewSongs.apply {
            setHasFixedSize(true)
            layoutManager = songLinearLayoutManager
            adapter = songAdapter
        }
        getSongs()
    }

    private fun getSongs() {
        val retrofit = provideRetrofit()

        val service = retrofit.create(CancionService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val cancionList = service.getCanciones()

                withContext(Dispatchers.Main) {
                    songAdapter.submitList(cancionList)
                }
            } catch (e: Exception) {
                Log.e("Cancion", e.message.toString())
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

    private fun setupBottomNav() {
        val origin = intent.getStringExtra("origin")

        mBinding.bottomNavigationView.selectedItemId =
            when (origin) {
                "library" -> R.id.action_library
                "search" -> R.id.action_search
                else -> R.id.action_search
            }

        mBinding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.action_search -> {
                    true
                }
                R.id.action_library -> {
                    startActivity(Intent(this, LibraryActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
    fun eliminarCancionLocal(cancion: Cancion) {
        Log.d("SongListActivity", "Intentando eliminar la canción: ${cancion.titulo}")

        SessionManager.eliminarCancionLocal(this, cancion)

        // Actualizar el RecyclerView con las canciones locales
        actualizarRecyclerViewConCancionesLocales(this)

        // Mostrar un mensaje de confirmación
        Toast.makeText(this, "Canción eliminada", Toast.LENGTH_SHORT).show()
    }

    fun actualizarRecyclerViewConCancionesLocales(context: Context) {
        val cancionesLocales = SessionManager.obtenerCancionesLocalesDesdePreferences(context)
        songAdapter.submitList(cancionesLocales)
        songAdapter.notifyDataSetChanged()
        Log.d("SongListActivity", "RecyclerView actualizado con canciones locales.")
    }

    override fun onClickPlaylist(id: Int) {
        // Not implemented yet
    }

    override fun onClickAlbum(id: Int) {
        // Not implemented yet
    }

    override fun onClickPodcast(id: Int) {
        // Not implemented yet
    }

    override fun onClickSong(id: Int) {
        val intent = Intent(this, SongDetailActivity::class.java)
        intent.putExtra("cancion", id)
        this.startActivity(intent)
    }

    override fun onSongAddedToPlaylist(cancion: Cancion) {
        // Agregar la canción localmente en SharedPreferences
        SessionManager.agregarCancionLocal(this, cancion)

        // Actualizar el RecyclerView con las canciones locales
        actualizarRecyclerViewConCancionesLocales(this)
    }
}
