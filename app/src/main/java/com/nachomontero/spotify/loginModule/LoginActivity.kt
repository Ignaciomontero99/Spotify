package com.nachomontero.spotify.loginModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etLoginUsername.text.toString()
            val password = binding.etLoginPassword.text.toString()

            // Aquí deberías hacer la llamada a la API
            loginUserFromApi(username, password)
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.etRegisterName.text.toString()
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()

            registerLocalUser(name, email, password)
        }

        binding.tvToggleView.setOnClickListener {
            isLoginMode = !isLoginMode
            updateViewMode()
        }

        updateViewMode()
    }

    private fun updateViewMode() {
        if (isLoginMode) {
            binding.layoutLogin.visibility = View.VISIBLE
            binding.layoutRegister.visibility = View.GONE
            binding.tvToggleView.text = "¿No tienes cuenta? Regístrate"
        } else {
            binding.layoutLogin.visibility = View.GONE
            binding.layoutRegister.visibility = View.VISIBLE
            binding.tvToggleView.text = "¿Ya tienes cuenta? Inicia sesión"
        }
    }

    private fun loginUserFromApi(username: String, password: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val loginService = retrofit.create(LoginService::class.java)
        val usuario = Usuario(username = username, password= password)

        lifecycleScope.launch {
            try {
                val user = loginService.login(usuario)
                SessionManager.saveSession(this@LoginActivity, user.id.toString(), "api")
                goToMain()
            } catch (e: Exception) {
                // Imprimir el mensaje de error completo para debug
                e.printStackTrace()
                Toast.makeText(this@LoginActivity, "Error en el login: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun registerLocalUser(name: String, username: String, password: String) {
        val prefs = getSharedPreferences("local_users", MODE_PRIVATE)
        prefs.edit().apply {
            putString("name", name)
            putString("username", username)
            putString("password", password)
            apply()
        }

        // Guardamos un id ficticio para local
        SessionManager.saveSession(this, (-1).toString(), "local")
        goToMain()
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}