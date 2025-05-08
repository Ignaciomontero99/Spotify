package com.nachomontero.spotify.libraryModule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.nachomontero.spotify.api.Cancion
import com.nachomontero.spotify.api.Playlist
import com.nachomontero.spotify.api.Usuario
import com.nachomontero.spotify.api.service.AlbumesService
import com.nachomontero.spotify.api.service.LoginService
import com.nachomontero.spotify.api.service.PodcastService
import com.nachomontero.spotify.loginModule.LoginActivity
import com.nachomontero.spotify.mainModule.adapter.AlbumesAdapter
import com.nachomontero.spotify.mainModule.adapter.PodcastAdapter
import com.nachomontero.spotify.sharedPreferences.SessionManager

class LibraryActivity : AppCompatActivity(), OnClickListener {
    private lateinit var mBinding: ActivityLibraryBinding

    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var playlistGridLayoutManager: GridLayoutManager

    private lateinit var albumAdapter: AlbumesAdapter
    private lateinit var albumLinearLayoutManager: LinearLayoutManager

    private lateinit var podcastAdapter: PodcastAdapter
    private lateinit var podcastLinearLayoutManager: LinearLayoutManager

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

        mBinding.btnAdd.setOnClickListener {
            showNewPlaylist()
        }

        mBinding.btnUser.setOnClickListener {
            getUsuario { usuario ->
                if (usuario != null) {
                    showUserInfo(usuario)
                } else {
                    Toast.makeText(this, "No se pudieron obtener los datos del usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }

        mBinding.btnPlaylist.setOnClickListener {
            setupRecyclerViewPlaylist()
        }

        mBinding.btnAlbums.setOnClickListener {
            setupRecyclerViewAlbum()
        }

        mBinding.btnPodcast.setOnClickListener {
            setupRecyclerViewPodcast()
        }

        val userId = SessionManager.getUserId(this)
        Log.d("USER_ID", "ID obtenido: $userId")
    }

    private fun setupRecyclerViewPlaylist() {
        playlistAdapter = PlaylistAdapter(this)
        playlistGridLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)

        mBinding.rvLibrary.apply {
            setHasFixedSize(true)
            layoutManager = playlistGridLayoutManager
            adapter = playlistAdapter
        }
        getPlaylist()

        playlistAdapter.onPlaylistLongClickListener = { playlist ->
            SessionManager.eliminarPlaylistLocal(this, playlist)
            actualizarRecyclerViewConPlaylists()
            Toast.makeText(this, "Playlist eliminada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerViewAlbum() {
        albumAdapter = AlbumesAdapter(this)
        albumLinearLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)

        mBinding.rvLibrary.apply {
            setHasFixedSize(true)
            layoutManager = albumLinearLayoutManager
            adapter = albumAdapter
        }
        getAlbum()
    }

    private fun setupRecyclerViewPodcast() {
        podcastAdapter = PodcastAdapter(this)
        podcastLinearLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)

        mBinding.rvLibrary.apply {
            setHasFixedSize(true)
            layoutManager = podcastLinearLayoutManager
            adapter = podcastAdapter
        }
        getPodcast()
    }

    private fun getPlaylist() {
        val userId = SessionManager.getUserId(this)
        if (userId == -1) {
            Log.e("MainActivity", "Usuario no logueado o ID no válido")
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PlaylistService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val playlists = service.getPlaylist(userId)

                withContext(Dispatchers.Main) {
                    Log.i("Playlist", playlists.toString())
                    playlistAdapter.submitList(playlists)
                }
            } catch (e: Exception) {
                Log.e("Playlist", "Error: ${e.message}", e)
            }
        }
    }

    private fun getPodcast() {
        val userId = SessionManager.getUserId(this)

        if (userId == -1) {
            Log.e("MainActivity", "Usuario no logueado o ID no válido")
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PodcastService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val podcastList = service.getPodcastFromUser(userId)
                withContext(Dispatchers.Main) {
                    Log.i("Podcast", podcastList.toString())
                    podcastAdapter.submitList(podcastList)
                }
            } catch (e: Exception) {
                Log.e("Podcast", e.message.toString())
            }
        }
    }

    private fun getAlbum() {
        val userId = SessionManager.getUserId(this)

        if (userId == -1) {
            Log.e("MainActivity", "Usuario no logueado o ID no válido")
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(AlbumesService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val albumList = service.getAlbumes(userId)
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

    private fun getUsuario(onResult: (Usuario?) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(LoginService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val usuario = service.getUser()
                withContext(Dispatchers.Main) {
                    onResult(usuario)
                }
            } catch (e: Exception) {
                Log.e("Usuario", "Error al obtener datos del usuario: ${e.localizedMessage}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LibraryActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    onResult(null)
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
        val dialogView = LayoutInflater.from(this).inflate(R.layout.new_playlist, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val etPlaylistName = dialogView.findViewById<EditText>(R.id.etPlaylistName)
        val btnCreate = dialogView.findViewById<Button>(R.id.btnCreate)

        btnCreate.setOnClickListener {
            val nombre = etPlaylistName.text.toString().trim()
            if (nombre.isNotEmpty()) {

                getUsuario { usuario ->
                    if (usuario != null) {
                        val nuevaPlaylist = Playlist(
                            id = (System.currentTimeMillis() % 100000).toInt(),
                            titulo = nombre
                        )
                        SessionManager.agregarPlaylistLocal(this, nuevaPlaylist)
                        actualizarRecyclerViewConPlaylists()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "No se pudo obtener el usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Nombre vacío", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }


    private fun actualizarRecyclerViewConPlaylists() {
        val playlists = SessionManager.obtenerPlaylistsDesdePreferences(this)
        playlistAdapter.submitList(playlists)
    }


    private fun showUserInfo(usuario: Usuario) {
        val dialogView = layoutInflater.inflate(R.layout.user_info, null)
        val dialog = AlertDialog.Builder(this, R.style.CustomDialog).create()
        dialog.setView(dialogView)
        dialog.setCancelable(true)

        // Acceder a cada campo
        fun setField(viewId: Int, label: String, value: String?) {
            val container = dialogView.findViewById<LinearLayout>(viewId)
            val tvLabel = container.findViewById<TextView>(R.id.tvLabel)
            val tvValue = container.findViewById<EditText>(R.id.tvValue)
            tvLabel.text = label
            tvValue.setText(value ?: "No disponible")
        }

        // Asignar datos
        setField(R.id.fieldUsername, "Nombre de usuario", usuario.username)
        setField(R.id.fieldEmail, "Email", usuario.email)
        setField(R.id.fieldGenero, "Género", usuario.genero)
        setField(R.id.fieldFecha, "Fecha de nacimiento", usuario.fechaNacimiento)
        setField(R.id.fieldPais, "País", usuario.pais)
        setField(R.id.fieldCodigoPostal, "Código Postal", usuario.codigoPostal)

        // Cerrar sesión
        dialogView.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            SessionManager.clearSession(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        dialog.window?.setBackgroundDrawable("#80000000".toColorInt().toDrawable())
        dialog.show()
    }

    override fun onClickPlaylist(id: Int) {
        val intent = Intent(this, SongListActivity::class.java)
        intent.putExtra("origin", "library")
        intent.putExtra("playlistId", id)
        startActivity(intent)
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
