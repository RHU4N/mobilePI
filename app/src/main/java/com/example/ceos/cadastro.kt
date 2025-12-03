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

class cadastro : AppCompatActivity(), HeaderMenuListener {
    private lateinit var home: TextView
    private lateinit var homeIMG: ImageView
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

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

        // Wire the form fields and button for registration
        val nomeField = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.nomeEditText)
        val emailField = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.emailEditText)
        val telefoneField = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.telefoneEditText)
        val senhaField = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.senhaEditText)
        val confirmarField = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.confirmarSenhaEditText)
        val cadastrarButton = findViewById<android.widget.Button>(R.id.cadastrarButton)

        cadastrarButton.setOnClickListener {
            val nome = nomeField.text?.toString()?.trim() ?: ""
            val email = emailField.text?.toString()?.trim() ?: ""
            val telefone = telefoneField.text?.toString()?.trim()
            val senha = senhaField.text?.toString() ?: ""
            val confirmar = confirmarField.text?.toString() ?: ""

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                android.widget.Toast.makeText(this@cadastro, "Preencha todos os campos", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (senha != confirmar) {
                android.widget.Toast.makeText(this@cadastro, "Senhas não conferem", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val resp = AuthRepository.register(nome, email, senha, telefone, null)
                    val prefs = getSharedPreferences("ceos_prefs", MODE_PRIVATE)
                    prefs.edit().putString("user_name", resp.nome).putString("user_email", resp.email).apply()
                    android.widget.Toast.makeText(this@cadastro, "Cadastrado: ${resp.email}", android.widget.Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@cadastro, home::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Log.e("Auth", "Register failed", e)
                    var message = e.localizedMessage ?: "Erro desconhecido"
                    if (e is HttpException) {
                        val errorBody = withContext(Dispatchers.IO) { e.response()?.errorBody()?.string() }
                        if (!errorBody.isNullOrBlank()) message = "HTTP ${e.code()}: ${errorBody}"
                        else message = "HTTP ${e.code()}: ${e.message()}"
                    }
                    android.widget.Toast.makeText(this@cadastro, "Erro: ${'$'}message", android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showMenu(anchor: android.view.View) {
        HeaderMenuUtil.showHeaderMenu(this, anchor)
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
                            android.widget.Toast.makeText(this@cadastro, "Cadastrado: ${resp.email}", android.widget.Toast.LENGTH_SHORT).show()
                            // Navegar para a tela inicial após cadastro
                            val intent = Intent(this@cadastro, home::class.java)
                            startActivity(intent)
                            finish()
                        } catch (e: Exception) {
                            Log.e("Auth", "Register failed", e)
                            var message = e.localizedMessage ?: "Erro desconhecido"
                            if (e is HttpException) {
                                val errorBody = withContext(Dispatchers.IO) { e.response()?.errorBody()?.string() }
                                if (!errorBody.isNullOrBlank()) message = "HTTP ${e.code()}: ${errorBody}"
                                else message = "HTTP ${e.code()}: ${e.message()}"
                            }
                            android.widget.Toast.makeText(this@cadastro, "Erro: ${message}", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ---------- FIX: ADD THIS MISSING FUNCTION ----------
    override fun showLoginDialog() {
        // Since you are already on the registration screen, you might want to navigate
        // to the login screen or simply show a toast message.
        // For now, let's just show a toast as a placeholder.
        android.widget.Toast.makeText(this, "Login dialog clicked", android.widget.Toast.LENGTH_SHORT).show()

        // You can also implement a full dialog here, similar to showRegisterDialog,
        // if you want to allow login from this screen.
    }
}
