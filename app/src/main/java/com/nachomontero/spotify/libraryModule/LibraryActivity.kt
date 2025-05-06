package com.nachomontero.spotify.libraryModule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.common.Constants
import com.nachomontero.spotify.api.service.PlaylistService
import com.nachomontero.spotify.databinding.ActivityLibraryBinding
import com.nachomontero.spotify.mainModule.MainActivity
import com.nachomontero.spotify.mainModule.adapter.PlaylistAdapter
import com.nachomontero.spotify.mainModule.listener.OnClickListener
import com.nachomontero.spotify.searchModule.SearchActivity
import com.nachomontero.spotify.songModule.SongListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.core.graphics.toColorInt
import androidx.core.graphics.drawable.toDrawable
import com.nachomontero.spotify.loginModule.LoginActivity
import com.nachomontero.spotify.sharedPreferences.SessionManager


class LibraryActivity : AppCompatActivity(), OnClickListener {
    private lateinit var mBinding: ActivityLibraryBinding

    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var playlistGridLayoutManager: GridLayoutManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mBinding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBottomNav()

        setupRecyclerView()

        mBinding.btnAdd.setOnClickListener {
            showNewPlaylist()
        }
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter(this)
        playlistGridLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)

        mBinding.rvLibrary.apply {
            setHasFixedSize(true)
            layoutManager = playlistGridLayoutManager
            adapter = playlistAdapter
        }
        getPlaylist()
    }

    private fun getPlaylist() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PlaylistService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val playlistList = service.getPlaylist()

                val playlists = playlistList.firstOrNull()?.map { it.playlist } ?: emptyList()

                withContext(Dispatchers.Main) {
                    Log.i("Playlist", playlists.toString())
                    playlistAdapter.submitList(playlists)
                }
            } catch (e: Exception) {
                Log.e("Playlist", e.message.toString())
                (e as? HttpException)?.let {
                    // Manejo de errores
                }
            }
        }
    }

    private fun setupBottomNav() {
        mBinding.bottomNavigationView.selectedItemId = R.id.action_library

        mBinding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.action_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
                R.id.action_library -> {
                    // Estás en tu biblioteca

                    true
                }
                else -> false
            }
        }
    }

    private fun showNewPlaylist() {
        val dialogView = layoutInflater.inflate(R.layout.new_playlist, null)
        val dialog = AlertDialog.Builder(this, R.style.CustomDialog).create()
        dialog.setView(dialogView)
        dialog.setCancelable(true)

        val etName = dialogView.findViewById<EditText>(R.id.etPlaylistName)
        val btnCreate = dialogView.findViewById<Button>(R.id.btnCreate)

        btnCreate.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isNotEmpty()) {
                getPlaylist()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Escribe un nombre", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.window?.setBackgroundDrawable("#80000000".toColorInt().toDrawable())
        dialog.show()
    }


    override fun onClickPlaylist(id: Int) {
        val intent = Intent(this, SongListActivity::class.java)
        intent.putExtra("origin", "library")
        startActivity(intent)
    }

    override fun onClickAlbum(id: Int) {
        TODO("Not yet implemented")
    }


}
