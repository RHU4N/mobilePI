package com.example.ceos

import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MathRepository {
    private val api = RetrofitClient.mathApi

    suspend fun estatisticaMedia(valores: List<Double>): EstatisticaResponse = withContext(Dispatchers.IO) {
        api.estatistica("media", EstatisticaRequest(valores))
    }

    suspend fun estatisticaMediana(valores: List<Double>): EstatisticaResponse = withContext(Dispatchers.IO) {
        api.estatistica("mediana", EstatisticaRequest(valores))
    }

    suspend fun estatisticaModa(valores: List<Double>): EstatisticaResponse = withContext(Dispatchers.IO) {
        api.estatistica("moda", EstatisticaRequest(valores))
    }

    suspend fun anguloGrausParaRadianos(valor: Double): AnguloResponse = withContext(Dispatchers.IO) {
        api.angulo("grauspararadianos", AnguloRequest(valor))
    }

    suspend fun anguloRadianosParaGraus(valor: Double): AnguloResponse = withContext(Dispatchers.IO) {
        api.angulo("radianosparagraus", AnguloRequest(valor))
    }

    suspend fun funcao(tipo: String, params: JsonObject): com.google.gson.JsonElement = withContext(Dispatchers.IO) {
        api.funcao(tipo, params)
    }

    suspend fun area(forma: String, params: JsonObject): FormaResponse = withContext(Dispatchers.IO) {
        api.area(forma, params)
    }

    suspend fun perimetro(forma: String, params: JsonObject): FormaResponse = withContext(Dispatchers.IO) {
        api.perimetro(forma, params)
    }

    suspend fun volume(forma: String, params: JsonObject): FormaResponse = withContext(Dispatchers.IO) {
        api.volume(forma, params)
    }

    suspend fun analise(tipo: String, n: Int, k: Int): AnaliseResponse = withContext(Dispatchers.IO) {
        api.analise(tipo, AnaliseRequest(n, k))
    }

    suspend fun energiaCinetica(m: Double, v: Double): EnergiaResponse = withContext(Dispatchers.IO) {
        api.energiaCinetica(EnergiaCineticaRequest(m, v))
    }

    suspend fun energiaTrabalho(params: JsonObject): EnergiaResponse = withContext(Dispatchers.IO) {
        api.energiaTrabalho(params)
    }

    suspend fun postGeneric(path: String, body: JsonObject): JsonObject = withContext(Dispatchers.IO) {
        api.postToPath(path, body)
    }
}
