package com.nachomontero.spotify.songModule

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
import com.nachomontero.spotify.api.service.CancionService
import com.nachomontero.spotify.databinding.ActivitySongListBinding
import com.nachomontero.spotify.libraryModule.LibraryActivity
import com.nachomontero.spotify.mainModule.MainActivity
import com.nachomontero.spotify.mainModule.adapter.SongAdapter
import com.nachomontero.spotify.mainModule.listener.OnClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class SongListActivity : AppCompatActivity(), OnClickListener {
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupBottomNav()

        setUpRecyclerViewSong()
    }

    private fun setUpRecyclerViewSong() {
        songAdapter = SongAdapter()
        songLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mBinding.recyclerViewSongs.apply {
            setHasFixedSize(true)
            layoutManager = songLinearLayoutManager
            adapter = songAdapter
        }
        getSongs()
    }

    private fun getSongs() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CancionService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val cancionList = service.getCanciones()
                withContext(Dispatchers.Main) {
                    Log.i("Cancion", cancionList.toString())
                    songAdapter.submitList(cancionList)
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

    fun onClickSong(id: Int) {
        // Para hacer clic a una canción y que me muestre su información
        TODO("Not yet implemented")
    }
}