package com.example.ceos

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlinx.coroutines.launch

class login : AppCompatActivity(), HeaderMenuListener {
    private lateinit var home: TextView
    private lateinit var homeIMG: ImageView
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        home = findViewById<TextView>(R.id.logo_text)
        homeIMG = findViewById<ImageView>(R.id.logo_image)
        menuIcon = findViewById(R.id.menu_icon)

        homeIMG.setOnClickListener {
            val intent = Intent(this, home::class.java)
            startActivity(intent)
            finish()
        }
        home.setOnClickListener {
            val intent = Intent(this, home::class.java)
            startActivity(intent)
            finish()
        }

        menuIcon.setOnClickListener {
            showMenu(it)
        }

        // Wire the form button to perform login (layout has its own fields)
        val emailField = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.emailEditText)
        val passwordField = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.passwordEditText)
        val loginButton = findViewById<android.widget.Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailField.text?.toString()?.trim() ?: ""
            val senha = passwordField.text?.toString() ?: ""
            if (email.isNotEmpty() && senha.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val resp = AuthRepository.login(email, senha)
                        val prefs = getSharedPreferences("ceos_prefs", MODE_PRIVATE)
                        prefs.edit().putString("auth_token", resp.token).putString("user_email", email).apply()
                        android.widget.Toast.makeText(this@login, "Logado com sucesso", android.widget.Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@login, home::class.java)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        Log.e("Auth", "Login failed", e)
                        var message = e.localizedMessage ?: "Erro desconhecido"
                        if (e is HttpException) {
                            val errorBody = withContext(Dispatchers.IO) { e.response()?.errorBody()?.string() }
                            if (!errorBody.isNullOrBlank()) message = "HTTP ${e.code()}: ${errorBody}"
                            else message = "HTTP ${e.code()}: ${e.message()}"
                        }
                        android.widget.Toast.makeText(this@login, "Erro: ${message}", android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                android.widget.Toast.makeText(this@login, "Preencha email e senha", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showMenu(anchor: android.view.View) {
        // Use centralized header menu that adapts to auth state
        HeaderMenuUtil.showHeaderMenu(this, anchor)
    }

    override fun showLoginDialog() {
        val emailInput = EditText(this)
        emailInput.hint = "email"
        val senhaInput = EditText(this)
        senhaInput.hint = "senha"
        senhaInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
            addView(emailInput)
            addView(senhaInput)
        }

        AlertDialog.Builder(this)
            .setTitle("Login")
            .setView(layout)
            .setPositiveButton("Entrar") { dialog, _ ->
                val email = emailInput.text.toString().trim()
                val senha = senhaInput.text.toString()
                if (email.isNotEmpty() && senha.isNotEmpty()) {
                    lifecycleScope.launch {
                        try {
                            val resp = AuthRepository.login(email, senha)
                            // Save token and email
                            val prefs = getSharedPreferences("ceos_prefs", MODE_PRIVATE)
                            prefs.edit().putString("auth_token", resp.token).putString("user_email", email).apply()
                            android.widget.Toast.makeText(this@login, "Logado com sucesso", android.widget.Toast.LENGTH_SHORT).show()
                            // Navega para a tela inicial após login
                            val intent = Intent(this@login, home::class.java)
                            startActivity(intent)
                            finish()
                        } catch (e: Exception) {
                            Log.e("Auth", "Login failed", e)
                            var message = e.localizedMessage ?: "Erro desconhecido"
                            if (e is HttpException) {
                                val errorBody = withContext(Dispatchers.IO) { e.response()?.errorBody()?.string() }
                                if (!errorBody.isNullOrBlank()) message = "HTTP ${e.code()}: ${errorBody}"
                                else message = "HTTP ${e.code()}: ${e.message()}"
                            }
                            android.widget.Toast.makeText(this@login, "Erro: ${message}", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun showRegisterDialog() {
        val emailInput = EditText(this)
        emailInput.hint = "email"
        val senhaInput = EditText(this)
        senhaInput.hint = "senha"
        senhaInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
            addView(emailInput)
            addView(senhaInput)
        }

        AlertDialog.Builder(this)
            .setTitle("Cadastrar")
            .setView(layout)
            .setPositiveButton("Cadastrar") { dialog, _ ->
                val email = emailInput.text.toString().trim()
                val senha = senhaInput.text.toString()
                if (email.isNotEmpty() && senha.isNotEmpty()) {
                    lifecycleScope.launch {
                        try {
                            val nome = email.substringBefore('@')
                            val resp = AuthRepository.register(nome, email, senha)
                            val prefs = getSharedPreferences("ceos_prefs", MODE_PRIVATE)
                            prefs.edit().putString("user_name", resp.nome).putString("user_email", resp.email).apply()
                            android.widget.Toast.makeText(this@login, "Cadastrado: ${resp.email}", android.widget.Toast.LENGTH_SHORT).show()
                            // Após cadastro, navegar também para a tela inicial
                            val intent = Intent(this@login, home::class.java)
                            startActivity(intent)
                            finish()
                        } catch (e: Exception) {
                            Log.e("Auth", "Register failed", e)
                            var message = e.localizedMessage ?: "Erro desconhecido"
                            if (e is HttpException) {
                                val errorBody = withContext(Dispatchers.IO) { e.response()?.errorBody()?.string() }
                                if (!errorBody.isNullOrBlank()) message = "HTTP ${e.code()}: ${errorBody}"
                                else message = "HTTP ${e.code()}: ${'$'}{e.message()}"
                            }
                            android.widget.Toast.makeText(this@login, "Erro: ${message}", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}