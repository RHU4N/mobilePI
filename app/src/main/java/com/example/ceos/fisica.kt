package com.example.ceos

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu

class fisica : AppCompatActivity() {
    private lateinit var home: TextView
    private lateinit var homeIMG: ImageView
    private lateinit var menuIcon: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fisica)

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
    }

    private fun showMenu(anchor: android.view.View) {
        HeaderMenuUtil.showHeaderMenu(this, anchor)
    }
}