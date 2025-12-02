package com.example.ceos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import java.io.IOException

class funcao : AppCompatActivity() {

    private lateinit var spinnerTipoFuncao: Spinner
    private lateinit var inputLayoutC: TextInputLayout
    private lateinit var editTextA: EditText
    private lateinit var editTextB: EditText
    private lateinit var editTextC: EditText
    private lateinit var editTextX: EditText
    private lateinit var buttonCalcular: Button
    private lateinit var buttonLimpar: Button
    private lateinit var textViewResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funcao)

        // Navegação
        val home = findViewById<TextView>(R.id.logo_text)
        val homeIMG = findViewById<ImageView>(R.id.logo_image)
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
        val breadcrumbHome = findViewById<TextView>(R.id.breadcrumb_home)
        breadcrumbHome.setOnClickListener{
            val intent = Intent(this, home::class.java)
            startActivity(intent)
            finish()
        }
        val breadcrumbMath = findViewById<TextView>(R.id.breadcrumb_matematica)
        breadcrumbMath.setOnClickListener{
            val intent = Intent(this, math::class.java)
            startActivity(intent)
            finish()
        }


        // Initialize views
        spinnerTipoFuncao = findViewById(R.id.spinnerTipoFuncao)
        inputLayoutC = findViewById(R.id.inputLayoutC)
        editTextA = findViewById(R.id.editTextA)
        editTextB = findViewById(R.id.editTextB)
        editTextC = findViewById(R.id.editTextC)
        editTextX = findViewById(R.id.editTextX)
        buttonCalcular = findViewById(R.id.buttonCalcular)
        buttonLimpar = findViewById(R.id.buttonLimpar)
        textViewResultado = findViewById(R.id.textViewResultado)

        // Setup Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.funcao_tipos,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTipoFuncao.adapter = adapter
        }

        spinnerTipoFuncao.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> { // 1º Grau
                        inputLayoutC.visibility = View.GONE
                    }
                    1 -> { // 2º Grau
                        inputLayoutC.visibility = View.VISIBLE
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        buttonCalcular.setOnClickListener {
            calcularFuncao()
        }

        buttonLimpar.setOnClickListener {
            editTextA.text.clear()
            editTextB.text.clear()
            editTextC.text.clear()
            editTextX.text.clear()
            textViewResultado.text = ""
        }
    }

    private fun calcularFuncao() {
        val tipoIndex = spinnerTipoFuncao.selectedItemPosition
        val tipo = if (tipoIndex == 0) "1-grau" else "2-grau"

        val aText = editTextA.text.toString()
        val bText = editTextB.text.toString()
        val xText = editTextX.text.toString()

        if (aText.isEmpty() || bText.isEmpty() || xText.isEmpty()) {
            textViewResultado.text = "Preencha todos os campos obrigatórios."
            return
        }

        val params = JsonObject()
        params.addProperty("a", aText.toDouble())
        params.addProperty("b", bText.toDouble())
        params.addProperty("x", xText.toDouble())

        if (tipo == "2-grau") {
            val cText = editTextC.text.toString()
            if (cText.isEmpty()) {
                textViewResultado.text = "Preencha o valor de c."
                return
            }
            params.addProperty("c", cText.toDouble())
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.funcao(tipo, params)
                if (response.has("resultado")) {
                    val resultado = response.get("resultado").asDouble
                    textViewResultado.text = "Resultado: $resultado"
                } else if (response.has("error")) {
                     textViewResultado.text = "Erro: ${response.get("error").asString}"
                } else {
                    textViewResultado.text = "Resposta inesperada da API."
                }
            } catch (e: IOException) {
                textViewResultado.text = "Erro de conexão. Verifique sua internet."
            } catch (e: Exception) {
                textViewResultado.text = "Ocorreu um erro: ${e.message}"
            }
        }
    }
}
