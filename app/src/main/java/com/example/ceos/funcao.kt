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
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import com.google.gson.JsonElement
import com.google.gson.JsonArray
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

    private lateinit var home: TextView
    private lateinit var homeIMG: ImageView
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funcao)

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

        menuIcon.setOnClickListener {
            showMenu(it)
        }
    }

    private fun showMenu(anchor: android.view.View) {
        HeaderMenuUtil.showHeaderMenu(this, anchor)
    }

    private fun calcularFuncao() {
        val tipoIndex = spinnerTipoFuncao.selectedItemPosition
        // Map frontend selection to backend route names
        val tipo = if (tipoIndex == 0) "linear" else "quadratica"

        val aText = editTextA.text.toString()
        val bText = editTextB.text.toString()
        val xText = editTextX.text.toString()

        // Validation differs by type:
        // - linear: requires a and b; x is optional (if omitted we can solve ax + b = 0 locally)
        // - quadratica: requires a, b, c; x is optional (if omitted, backend should return roots)
        if (tipo == "linear") {
            if (aText.isEmpty() || bText.isEmpty()) {
                textViewResultado.text = "Preencha os campos a e b para função 1º grau. x é opcional (se vazio, será resolvido)."
                return
            }
        } else {
            // quadratica
            val cText = editTextC.text.toString()
            if (aText.isEmpty() || bText.isEmpty() || cText.isEmpty()) {
                textViewResultado.text = "Preencha os campos a, b e c para função 2º grau. x é opcional (se vazio, serão retornadas as raízes)."
                return
            }
        }

        val params = JsonObject()
        params.addProperty("a", aText.toDouble())
        params.addProperty("b", bText.toDouble())
        if (tipo == "quadratica") {
            val cText = editTextC.text.toString()
            params.addProperty("c", cText.toDouble())
            if (xText.isNotEmpty()) params.addProperty("x", xText.toDouble())
        } else {
            // linear: add x only if provided
            if (xText.isNotEmpty()) params.addProperty("x", xText.toDouble())
        }

        lifecycleScope.launch {
            try {
                val response = MathRepository.funcao(tipo, params)
                // Normalize response similar to web front implementation
                val display = normalizeFuncaoResponse(response)
                textViewResultado.text = display
            } catch (e: Exception) {
                // If API failed and it's linear with missing x, compute root locally
                if (tipo == "linear" && xText.isEmpty()) {
                    try {
                        val a = aText.toDouble()
                        val b = bText.toDouble()
                        if (a == 0.0) {
                            textViewResultado.text = "Equação degenerada (a = 0)."
                        } else {
                            val root = -b / a
                            textViewResultado.text = "Resultado (resolvido localmente): x = ${root}"
                        }
                    } catch (inner: Exception) {
                        textViewResultado.text = "Não foi possível resolver localmente: ${inner.message}"
                    }
                } else if (e is IOException) {
                    textViewResultado.text = "Erro de conexão. Verifique sua internet."
                } else {
                    textViewResultado.text = "Ocorreu um erro: ${e.message}"
                }
            }
        }
    }

    private fun normalizeFuncaoResponse(respElem: JsonElement?): String {
        if (respElem == null || respElem.isJsonNull) return "Resposta vazia"
        // If it's a primitive or array, delegate to formatter
        if (!respElem.isJsonObject) {
            return formatJsonElement(respElem)
        }

        val resp = respElem.asJsonObject
        // Prefer 'resultado' field
        val candidateKeys = listOf("resultado", "result", "value", "raizes", "raiz")
        for (k in candidateKeys) {
            if (resp.has(k)) {
                return formatJsonElement(resp.get(k))
            }
        }
        // If the object has a single primitive property, return it
        val entries = resp.entrySet()
        if (entries.size == 1) {
            val v = entries.first().value
            return formatJsonElement(v)
        }
        // Fallback: return whole payload as string
        return resp.toString()
    }

    private fun formatJsonElement(elem: JsonElement?): String {
        if (elem == null || elem.isJsonNull) return "Resultado: null"
        if (elem.isJsonPrimitive) {
            val prim = elem.asJsonPrimitive
            return if (prim.isNumber) "Resultado: ${prim.asNumber}" else "Resultado: ${prim.asString}"
        }
        if (elem.isJsonArray) {
            val arr = elem.asJsonArray
            val parts = arr.map { e ->
                when {
                    e.isJsonPrimitive && e.asJsonPrimitive.isNumber -> e.asNumber.toString()
                    e.isJsonPrimitive -> e.asString
                    else -> e.toString()
                }
            }
            return "Resultado: ${parts.joinToString(", ")}"
        }
        return "Resultado: ${elem.toString()}"
    }
}
