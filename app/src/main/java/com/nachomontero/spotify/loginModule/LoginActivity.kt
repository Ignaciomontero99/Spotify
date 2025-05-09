package com.nachomontero.spotify.loginModule

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nachomontero.spotify.api.Usuario
import com.nachomontero.spotify.api.common.Constants
import com.nachomontero.spotify.api.service.LoginService
import com.nachomontero.spotify.databinding.ActivityLoginBinding
import com.nachomontero.spotify.mainModule.MainActivity
import com.nachomontero.spotify.sharedPreferences.SessionManager
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val id = SessionManager.getUserId(this)
            val username = binding.etLoginUsername.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()

            // Validación básica de campos vacíos
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = Usuario(id = id, username = username, password = password)
            loginUserFromApi(usuario)
        }
    }

    private fun loginUserFromApi(usuario: Usuario) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val userService = retrofit.create(LoginService::class.java)

        lifecycleScope.launch {
            try {
                val user = userService.getUser()

                if (usuario.username == user.username && usuario.password == user.password) {
                    SessionManager.saveSession(this@LoginActivity, user.id.toString(), "api")
                    goToMain()
                } else {
                    showToast("Usuario o contraseña incorrectos")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Error al conectar con el servidor")
            }
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Finaliza LoginActivity para evitar volver con el botón atrás
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
