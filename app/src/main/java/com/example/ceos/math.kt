package com.example.ceos

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class math : AppCompatActivity() {
    private lateinit var home: TextView
    private lateinit var homeIMG: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_math)

        home = findViewById<TextView>(R.id.logo_text)
        homeIMG = findViewById<ImageView>(R.id.logo_image)
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


    }
}