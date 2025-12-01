package com.example.ceos

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class home : AppCompatActivity() {

    private lateinit var mathButton: MaterialButton
    private lateinit var fisicaButton: MaterialButton
    private lateinit var quimicaButton: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        mathButton = findViewById(R.id.math_button)
        fisicaButton = findViewById(R.id.fisica_button)
        quimicaButton = findViewById(R.id.quimica_button)

        mathButton.setOnClickListener {
            // Navegue para a tela de matemática
            val intent = Intent(this, math::class.java)
            startActivity(intent)
        }

        fisicaButton.setOnClickListener {
            // Navegue para a tela de física
            val intent = Intent(this, fisica::class.java)
            startActivity(intent)
        }

        quimicaButton.setOnClickListener {
            // Navegue para a tela de química
            val intent = Intent(this, quimica::class.java)
            startActivity(intent)
        }

    }
}