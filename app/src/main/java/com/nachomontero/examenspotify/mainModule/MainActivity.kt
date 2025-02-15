package com.nachomontero.examenspotify.mainModule

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.nachomontero.examenspotify.R
import com.nachomontero.examenspotify.databinding.ActivityMainBinding
import com.nachomontero.examenspotify.mainModule.Adapter.PlaylistAdapter
import com.nachomontero.examenspotify.mainModule.viewModel.PlaylistViewModel
import com.nachomontero.examenspotify.profileModule.ProfileActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: PlaylistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.fetchPlaylists()
        val adapter = PlaylistAdapter(emptyList()) { playlist ->
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("PLAYLIST_ID", playlist.id)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.playlists.observe(this) { playlists ->
            adapter.updatePlaylists(playlists)
        }

        binding.fabProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}