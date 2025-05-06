package com.nachomontero.spotify.mainModule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.common.Constants
import com.nachomontero.spotify.api.service.AlbumesService
import com.nachomontero.spotify.api.service.PlaylistService
import com.nachomontero.spotify.api.service.PodcastService
import com.nachomontero.spotify.databinding.ActivityMainBinding
import com.nachomontero.spotify.libraryModule.LibraryActivity
import com.nachomontero.spotify.loginModule.LoginActivity
import com.nachomontero.spotify.mainModule.adapter.AlbumesAdapter
import com.nachomontero.spotify.mainModule.adapter.PlaylistAdapter
import com.nachomontero.spotify.mainModule.adapter.PodcastAdapter
import com.nachomontero.spotify.mainModule.listener.OnClickListener
import com.nachomontero.spotify.searchModule.SearchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java
import com.nachomontero.spotify.sharedPreferences.SessionManager
import com.nachomontero.spotify.songModule.SongListActivity


class MainActivity : AppCompatActivity(),OnClickListener {
    private lateinit var mBinding: ActivityMainBinding

    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var zonesLinearLayoutManager: LinearLayoutManager

    private lateinit var albumAdapter: AlbumesAdapter
    private lateinit var albumLinearLayoutManager: LinearLayoutManager

    private lateinit var podcastAdapter: PodcastAdapter
    private lateinit var podcastLinearLayoutManager: LinearLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBottomNav()

        setUpRecyclerViewPlaylist()
        setUpRecyclerViewAlbum()
        setUpRecyclerViewPodcast()
    }

    private fun setUpRecyclerViewPodcast() {
        podcastAdapter = PodcastAdapter()
        podcastLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mBinding.rvPodcast.apply {
            setHasFixedSize(true)
            layoutManager = podcastLinearLayoutManager
            adapter = podcastAdapter
        }
        val userType = SessionManager.getUserType(this)
        if (userType == "api") {
            getPodcast()
        } else {
            // Mostrar mensaje
            mBinding.tvTitlePodcast.text = "No hay podcasts disponibles"
        }

    }

    private fun getPodcast() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PodcastService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val podcastList = service.getPodcast()
                withContext(Dispatchers.Main) {
                    Log.i("Podcast", podcastList.toString())
                    podcastAdapter.submitList(podcastList)
                }
            } catch (e: Exception) {
                Log.e("Podcast", e.message.toString())
                (e as? HttpException)?.let {

                }
            }
        }
    }

    private fun setUpRecyclerViewAlbum() {
        albumAdapter = AlbumesAdapter(this)
        albumLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mBinding.rvAlbum.apply {
            setHasFixedSize(true)
            layoutManager = albumLinearLayoutManager
            adapter = albumAdapter
        }

        val userType = SessionManager.getUserType(this)
        if (userType == "api") {
            getAlbumes()
        } else {
            // Mostrar mensaje
            mBinding.tvTitleAlbum.text = "No hay álbumes disponibles"
            }
    }

    private fun getAlbumes() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(AlbumesService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val albumList = service.getAlbumes()
                withContext(Dispatchers.Main) {
                    Log.i("Album", albumList.toString())
                    albumAdapter.submitList(albumList)
                }
            } catch (e: Exception) {
                Log.e("Album", e.message.toString())
                (e as? HttpException)?.let {

                }
            }
        }
    }

    private fun setUpRecyclerViewPlaylist() {
        playlistAdapter = PlaylistAdapter(this)
        zonesLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mBinding.rvPlaylist.apply {
            setHasFixedSize(true)
            layoutManager = zonesLinearLayoutManager
            adapter = playlistAdapter
        }
        val userType = SessionManager.getUserType(this)
        if (userType == "api") {
            getPlaylist()
        } else {
            // Mostrar mensaje
            mBinding.tvTitlePlaylist.text = "No hay playlists disponibles"
        }
    }

    private fun getPlaylist() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PlaylistService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Aquí obtenemos la lista de listas de playlists
                val playlistList = service.getPlaylist()

                // Tomamos el primer elemento de la lista (la lista de playlists)
                val playlists = playlistList.firstOrNull()?.map { it.playlist } ?: emptyList()

                withContext(Dispatchers.Main) {
                    Log.i("Playlist", playlists.toString())
                    playlistAdapter.submitList(playlists) // Pasamos la lista de playlists al adaptador
                }
            } catch (e: Exception) {
                Log.e("Playlist", e.message.toString())
                (e as? HttpException)?.let {
                    // Manejo de errores
                }
            }
        }
    }

    // Configura el BottomNavigationView
    private fun setupBottomNav() {
        mBinding.bottomNavigationView.selectedItemId = R.id.action_home

        mBinding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    // Ya estás en Home
                    true
                }
                R.id.action_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
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

    override fun onClickPlaylist(id: Int) {
        val intent = Intent(this, SongListActivity::class.java)
        intent.putExtra("origin", "home")
        startActivity(intent)
    }

    override fun onClickAlbum(id: Int) {
        val intent = Intent(this, SongListActivity::class.java)
        intent.putExtra("origin", "home")
        startActivity(intent)
    }

}