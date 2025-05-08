package com.nachomontero.spotify.podcastModule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.Cancion
import com.nachomontero.spotify.api.Podcast
import com.nachomontero.spotify.api.common.Constants
import com.nachomontero.spotify.api.service.PodcastService
import com.nachomontero.spotify.databinding.ActivityPodcastListBinding
import com.nachomontero.spotify.episodeModule.EpisodeListActivity
import com.nachomontero.spotify.libraryModule.LibraryActivity
import com.nachomontero.spotify.mainModule.MainActivity
import com.nachomontero.spotify.mainModule.adapter.PodcastAdapter
import com.nachomontero.spotify.mainModule.listener.OnClickListener
import com.nachomontero.spotify.sharedPreferences.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PodcastListActivity : AppCompatActivity(), OnClickListener {
    private lateinit var mBinding: ActivityPodcastListBinding

    private lateinit var podcastAdapter: PodcastAdapter
    private lateinit var podcastGridLayoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mBinding = ActivityPodcastListBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBottomNav()

        setUpRecyclerViewPodcast()

    }

    private fun setUpRecyclerViewPodcast() {
        podcastAdapter = PodcastAdapter(this)
        podcastGridLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)

        mBinding.recyclerViewPodcasts.apply {
            setHasFixedSize(true)
            layoutManager = podcastGridLayoutManager
            adapter = podcastAdapter
        }

        getPodcast()
    }

    private fun getPodcast() {
        val userId = SessionManager.getUserId(this)
        val origin = intent.getStringExtra("origin")

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PodcastService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val podcastList = if (origin == "search"){
                    service.getPodcast()
                } else {
                    service.getPodcastFromUser(userId)
                }
                withContext(Dispatchers.Main) {
                    Log.i("Podcast", podcastList.toString())
                    podcastAdapter.submitList(podcastList as List<Podcast?>?)
                }
            } catch (e: Exception) {
                Log.e("Podcast", e.message.toString())
            }
        }
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
        TODO("Not yet implemented")
    }

    override fun onClickAlbum(id: Int) {
        TODO("Not yet implemented")
    }

    override fun onClickPodcast(id: Int) {
        val intent = Intent(this, EpisodeListActivity::class.java)
        intent.putExtra("origin", "search")
        intent.putExtra("podcastId", id)
        startActivity(intent)
    }

    override fun onClickSong(id: Int) {
        TODO("Not yet implemented")
    }

    override fun onSongAddedToPlaylist(cancion: Cancion) {
        TODO("Not yet implemented")
    }
}