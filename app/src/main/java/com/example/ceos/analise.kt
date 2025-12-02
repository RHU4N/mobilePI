package com.example.ceos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class analise : AppCompatActivity() {
    private lateinit var home: TextView
    private lateinit var homeIMG: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_analise)

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

        val spinnerTipo = findViewById<Spinner>(R.id.spinnerTipo)
        val inputN = findViewById<TextInputEditText>(R.id.editTextN)
        val inputK = findViewById<TextInputEditText>(R.id.editTextK)
        val inputLayoutK = findViewById<View>(R.id.inputLayoutK)
        val buttonCalcular = findViewById<Button>(R.id.buttonCalcular)
        val buttonLimpar = findViewById<Button>(R.id.buttonLimpar)
        val textResultado = findViewById<TextView>(R.id.textViewResultado)

        // Show/hide k field depending on selected tipo
        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val tipo = parent?.getItemAtPosition(position).toString()
                // Permutação only needs n; Arranjo and Combinação need k
                if (tipo.contains("Permut", ignoreCase = true)) {
                    inputLayoutK.visibility = View.GONE
                } else {
                    inputLayoutK.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        buttonCalcular.setOnClickListener {
            val tipoSelecionado = spinnerTipo.selectedItem.toString()
            val mappedTipo = mapTipoToApi(tipoSelecionado)

            val nText = inputN.text?.toString()?.trim().orEmpty()
            val kText = inputK.text?.toString()?.trim().orEmpty()

            if (nText.isEmpty()) {
                textResultado.text = "Informe o valor de n"
                return@setOnClickListener
            }

            val n = try { nText.toInt() } catch (e: Exception) {
                textResultado.text = "n inválido"
                return@setOnClickListener
            }

            // For permutation (fatorial) k is not required; for others ensure k provided
            var k = 0
            if (!tipoSelecionado.contains("Permut", ignoreCase = true)) {
                if (kText.isEmpty()) {
                    textResultado.text = "Informe o valor de k"
                    return@setOnClickListener
                }
                k = try { kText.toInt() } catch (e: Exception) {
                    textResultado.text = "k inválido"
                    return@setOnClickListener
                }
            } else {
                // For permutation, set k = n (not used on backend fatorial but keep numeric)
                k = n
            }

            // Disable buttons while loading
            buttonCalcular.isEnabled = false
            buttonLimpar.isEnabled = false
            textResultado.text = "Carregando..."

            lifecycleScope.launch {
                try {
                    val response = MathRepository.analise(mappedTipo, n, k)
                    textResultado.text = "Resultado: ${response.resultado}"
                } catch (e: Exception) {
                    textResultado.text = "Erro: ${e.message}"
                } finally {
                    buttonCalcular.isEnabled = true
                    buttonLimpar.isEnabled = true
                }
            }
        }

        buttonLimpar.setOnClickListener {
            inputN.setText("")
            inputK.setText("")
            textResultado.text = ""
        }
    }

    private fun mapTipoToApi(display: String): String {
        return when {
            display.contains("Permut", ignoreCase = true) -> "fatorial"
            display.contains("Arranjo", ignoreCase = true) -> "arranjo"
            display.contains("Combin", ignoreCase = true) -> "combinacao"
            else -> display.lowercase()
        }
    }
}