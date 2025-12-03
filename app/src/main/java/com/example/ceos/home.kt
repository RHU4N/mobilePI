package com.example.ceos

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.button.MaterialButton

class home : AppCompatActivity() {

    private lateinit var mathButton: MaterialButton
    private lateinit var fisicaButton: MaterialButton
    private lateinit var quimicaButton: MaterialButton
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mathButton = findViewById(R.id.math_button)
        fisicaButton = findViewById(R.id.fisica_button)
        quimicaButton = findViewById(R.id.quimica_button)
        menuIcon = findViewById(R.id.menu_icon)

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

        menuIcon.setOnClickListener {
            showMenu(it)
        }
    }

    private fun showMenu(anchor: android.view.View) {
        HeaderMenuUtil.showHeaderMenu(this, anchor)
    }
}