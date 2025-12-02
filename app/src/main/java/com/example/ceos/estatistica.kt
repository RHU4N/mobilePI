package com.example.ceos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class estatistica : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_estatistica)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinnerTipo: Spinner = findViewById(R.id.spinnerTipo)
        val editTextNumeros: EditText = findViewById(R.id.editTextNumeros)
        val buttonCalcular: Button = findViewById(R.id.buttonCalcular)
        val buttonLimpar: Button = findViewById(R.id.buttonLimpar)
        val textViewResultado: TextView = findViewById(R.id.textViewResultado)

        ArrayAdapter.createFromResource(
            this,
            R.array.estatistica_tipos,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTipo.adapter = adapter
        }

        buttonCalcular.setOnClickListener {
            val tipo = spinnerTipo.selectedItem.toString()
            val numerosStr = editTextNumeros.text.toString()
            if (numerosStr.isNotEmpty()) {
                // Parse numbers (accept comma or dot as decimal separator)
                val numeros = numerosStr.split(",").mapNotNull {
                    it.trim().replace(',', '.').takeIf { s -> s.isNotEmpty() }?.toDoubleOrNull()
                }
                if (numeros.isEmpty()) {
                    textViewResultado.text = "Por favor, insira números válidos."
                    return@setOnClickListener
                }

                    // Use API for supported operations (media, mediana, moda)
                lifecycleScope.launch {
                    try {
                        when (tipo) {
                            "Média" -> {
                                val resp = MathRepository.estatisticaMedia(numeros)
                                    val r = resp.resultado
                                    textViewResultado.text = if (r != null && r.isJsonPrimitive) "Resultado: ${r.asDouble}" else "Resposta inesperada"
                            }
                            "Mediana" -> {
                                val resp = MathRepository.estatisticaMediana(numeros)
                                    val r = resp.resultado
                                    textViewResultado.text = if (r != null && r.isJsonPrimitive) "Resultado: ${r.asDouble}" else "Resposta inesperada"
                            }
                            "Moda" -> {
                                val resp = MathRepository.estatisticaModa(numeros)
                                val r = resp.resultado
                                textViewResultado.text = formatResultadoJson(r)
                            }
                            else -> {
                                textViewResultado.text = "Tipo não reconhecido."
                            }
                        }
                    } catch (e: Exception) {
                        textViewResultado.text = "Erro ao chamar API: ${'$'}{e.message}"
                    }
                }
            } else {
                textViewResultado.text = "Por favor, insira os números."
            }
        }

        buttonLimpar.setOnClickListener {
            editTextNumeros.text.clear()
            textViewResultado.text = ""
        }
    }

        private fun formatResultadoJson(elem: com.google.gson.JsonElement?): String {
            if (elem == null || elem.isJsonNull) return "Resultado: sem valor (null)"
            return if (elem.isJsonArray) {
                val arr = elem.asJsonArray.mapNotNull { je ->
                    when {
                        je.isJsonPrimitive && je.asJsonPrimitive.isNumber -> je.asDouble.toString()
                        je.isJsonPrimitive && je.asJsonPrimitive.isString -> je.asString
                        else -> je.toString()
                    }
                }
                "Resultado: ${arr.joinToString(", ") }"
            } else if (elem.isJsonPrimitive) {
                val prim = elem.asJsonPrimitive
                if (prim.isNumber) return "Resultado: ${prim.asDouble}"
                if (prim.isString) return "Resultado: ${prim.asString}"
                return "Resultado: ${prim.toString()}"
            } else {
                return "Resultado: ${elem.toString()}"
            }
        }
    // Note: all calculations are delegated to the API; local calculation functions removed.
}