package com.example.ceos

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.cardview.widget.CardView

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

        //abre a tela de analise
        val analiseCard = findViewById<CardView?>(R.id.anal_comb_card)
        analiseCard?.setOnClickListener {
            val intent = Intent(this, analise::class.java)
            startActivity(intent)
        }

        // Abre a tela de Estatística
        val estatisticaCard = findViewById<CardView?>(R.id.estatistica_card)
        estatisticaCard?.setOnClickListener {
            val intent = Intent(this, estatistica::class.java)
            startActivity(intent)
        }

        // Adiciona leitura para o card de "Função"
        val funcaoCard = findViewById<CardView?>(R.id.funcao_card)
        funcaoCard?.setOnClickListener {
            val intent = Intent(this, funcao::class.java)
            startActivity(intent)
        }


    }
}