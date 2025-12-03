package com.example.ceos

import com.google.gson.JsonObject
import com.google.gson.JsonElement
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

// Estatística
data class EstatisticaRequest(val valores: List<Double>)
data class EstatisticaResponse(val tipo: String, val resultado: JsonElement?)

// Angulo
data class AnguloRequest(val valor: Double)
data class AnguloResponse(val tipo: String, val resultado: Double)

// Energia
data class EnergiaCineticaRequest(val m: Double, val v: Double)
data class EnergiaResponse(val resultado: Double)

// Perímetro / Área / Volume responses
data class FormaResponse(val forma: String, val perimetro: Double? = null, val area: Double? = null, val volume: Double? = null)

// Analise combinatória
data class AnaliseRequest(val n: Int, val k: Int)
data class AnaliseResponse(val tipo: String, val resultado: Double)

// Auth / Users
data class LoginRequest(val email: String, val senha: String)
data class LoginResponse(val token: String)

data class RegisterRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val telefone: String? = null,
    val assinante: Boolean? = null
)

data class RegisterResponse(val nome: String, val email: String)

/**
 * Retrofit service mapping the `mathApi` routes.
 */
interface ApiService {
    // Estatistica: POST /estatistica/{tipo}
    @POST("estatistica/{tipo}")
    suspend fun estatistica(@Path("tipo") tipo: String, @Body request: EstatisticaRequest): EstatisticaResponse

    // Angulo: POST /angulo/{tipo}
    @POST("angulo/{tipo}")
    suspend fun angulo(@Path("tipo") tipo: String, @Body request: AnguloRequest): AnguloResponse

    // Funcao: POST /funcao/{tipo} - body is generic JSON (a,b,c,x etc.)
    @POST("funcao/{tipo}")
    suspend fun funcao(@Path("tipo") tipo: String, @Body params: JsonObject): com.google.gson.JsonElement

    // Area: POST /area/{forma}
    @POST("area/{forma}")
    suspend fun area(@Path("forma") forma: String, @Body params: JsonObject): FormaResponse

    // Perimetro: POST /perimetro/{forma}
    @POST("perimetro/{forma}")
    suspend fun perimetro(@Path("forma") forma: String, @Body params: JsonObject): FormaResponse

    // Volume: POST /volume/{forma}
    @POST("volume/{forma}")
    suspend fun volume(@Path("forma") forma: String, @Body params: JsonObject): FormaResponse

    // Analise combinatória: POST /analise/{tipo}
    @POST("analise/{tipo}")
    suspend fun analise(@Path("tipo") tipo: String, @Body request: AnaliseRequest): AnaliseResponse

    // Energia examples
    @POST("energia/cinetica")
    suspend fun energiaCinetica(@Body request: EnergiaCineticaRequest): EnergiaResponse

    @POST("energia/trabalho")
    suspend fun energiaTrabalho(@Body params: JsonObject): EnergiaResponse

    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Create user (register)
    @POST("users")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    // Generic POST helper
    @POST("{path}")
    suspend fun postToPath(@Path(value = "path", encoded = true) path: String, @Body body: JsonObject): JsonObject
}
