package com.nachomontero.spotify.searchModule

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.Cancion
import com.nachomontero.spotify.databinding.ActivityLibraryBinding
import com.nachomontero.spotify.databinding.ActivitySearchBinding
import com.nachomontero.spotify.episodeModule.EpisodeListActivity
import com.nachomontero.spotify.libraryModule.LibraryActivity
import com.nachomontero.spotify.mainModule.MainActivity
import com.nachomontero.spotify.mainModule.listener.OnClickListener
import com.nachomontero.spotify.podcastModule.PodcastListActivity
import com.nachomontero.spotify.songModule.SongListActivity

class SearchActivity : AppCompatActivity(), OnClickListener {
    private lateinit var mBinding: ActivitySearchBinding
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

    override fun onSongAddedToPlaylist(cancion: Cancion) {
        TODO("Not yet implemented")
    }

}