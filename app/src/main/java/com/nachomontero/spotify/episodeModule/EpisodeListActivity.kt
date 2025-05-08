package com.nachomontero.spotify.episodeModule

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
import com.nachomontero.spotify.api.common.Constants
import com.nachomontero.spotify.api.service.EpisodoService
import com.nachomontero.spotify.databinding.ActivityEpisodeListBinding
import com.nachomontero.spotify.databinding.ActivitySongListBinding
import com.nachomontero.spotify.libraryModule.LibraryActivity
import com.nachomontero.spotify.mainModule.MainActivity
import com.nachomontero.spotify.mainModule.adapter.EpisodeAdapter
import com.nachomontero.spotify.songModule.SongListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EpisodeListActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityEpisodeListBinding

    private lateinit var episodeAdapter: EpisodeAdapter
    private lateinit var episodeLinearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        mBinding = ActivityEpisodeListBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        val podcastId = intent.getIntExtra("podcastId", -1)
        
        setUpRecyclerViewEpisode(podcastId)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBottomNav()
    }

    private fun setUpRecyclerViewEpisode(podcastId: Int) {
        episodeAdapter = EpisodeAdapter()
        episodeLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mBinding.recyclerViewEpisodes.apply {
            setHasFixedSize(true)
            layoutManager = episodeLinearLayoutManager
            adapter = episodeAdapter
        }
        getEpisodesFromPodcast(podcastId)
    }

    private fun getEpisodesFromPodcast(podcastId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(EpisodoService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val episodeList = service.getEpisodiosFromPodcast(podcastId)

                withContext(Dispatchers.Main) {
                    if (episodeList.isEmpty()) {
                        Log.d("EpisodeActivity", "Episodios recibidos: ${episodeList.size}")
                        Toast.makeText(this@EpisodeListActivity, "No hay episodios en este podcast", Toast.LENGTH_SHORT).show()

                        Log.i("Episode", "No hay episodios en este podcast")
                    } else {
                        Log.d("EpisodeActivity", "Episodios recibidos: ${episodeList.size}")

                        episodeAdapter.submitList(episodeList)
                    }
                }
            } catch (e: Exception) {
                Log.e("Cancion", e.message.toString())
                (e as? HttpException)?.let {

                }
            }
        }

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
                    // Ya estás en esta activity
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
}