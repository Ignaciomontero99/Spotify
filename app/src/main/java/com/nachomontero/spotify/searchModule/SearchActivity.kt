package com.nachomontero.spotify.searchModule

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.common.Constants
import com.nachomontero.spotify.api.service.CancionService
import com.nachomontero.spotify.databinding.ActivitySearchBinding
import com.nachomontero.spotify.libraryModule.LibraryActivity
import com.nachomontero.spotify.mainModule.MainActivity
import com.nachomontero.spotify.mainModule.adapter.SongAdapter
import com.nachomontero.spotify.mainModule.listener.OnClickListener
import com.nachomontero.spotify.podcastModule.PodcastListActivity
import com.nachomontero.spotify.songModule.SongListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity(), OnClickListener {
    private lateinit var mBinding: ActivitySearchBinding

    private lateinit var songAdapter: SongAdapter
    private lateinit var songLinearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupBottomNav()

        mBinding.cardSongs.setOnClickListener {
            onClickSong()
        }

        mBinding.cardPodcast.setOnClickListener {
            onClickPodcast()
        }

        setUpRecyclerViewSong()
    }

    private fun setupBottomNav() {
        mBinding.bottomNavigationView.selectedItemId = R.id.action_search

        mBinding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.action_search -> {
                    // Estás en el buscador

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

    private fun setUpRecyclerViewSong() {
        songAdapter = SongAdapter(this, null, null)
        songLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvSongSearch.apply {
            setHasFixedSize(true)
            layoutManager = songLinearLayoutManager
            adapter = songAdapter
        }

        mBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    buscarCanciones(it.toString())
                }
            }
        })
    }

    private fun buscarCanciones(query: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CancionService::class.java)

        if (query.isEmpty()) return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val listSong = service.getSpecificSong(query)

                val songFound = listSong.filter {
                    it.titulo.contains(query, ignoreCase = true)
                }

                withContext(Dispatchers.Main) {


                    songAdapter = SongAdapter(this@SearchActivity)
                    Log.d("BuscarCanciones", "Cantidad de resultados: ${songFound.size}")
                    songAdapter.submitList(songFound)
                    mBinding.rvSongSearch.adapter = songAdapter
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SearchActivity,
                        "Error al buscar canciones",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("buscadorSong", "Error al buscar canciones", e)
                }
            }
        }
    }

    fun onClickPodcast() {
        val intent = Intent(this, PodcastListActivity::class.java)
        intent.putExtra("origin", "search")
        startActivity(intent)
    }

    fun onClickSong() {
        val intent = Intent(this, SongListActivity::class.java)
        intent.putExtra("origin", "search")
        startActivity(intent)
    }

    override fun onClickPlaylist(id: Int) {
        TODO("Not yet implemented")
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

    override fun onClickEpisode(id: Int) {
        TODO("Not yet implemented")
    }
}