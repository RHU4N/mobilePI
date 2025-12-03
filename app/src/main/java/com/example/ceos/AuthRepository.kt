package com.example.ceos

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AuthRepository {
    private val api = RetrofitClient.authApi

    suspend fun login(email: String, senha: String): LoginResponse = withContext(Dispatchers.IO) {
        api.login(LoginRequest(email, senha))
    }

    suspend fun register(nome: String, email: String, senha: String, telefone: String? = null, assinante: Boolean? = null): RegisterResponse = withContext(Dispatchers.IO) {
        api.register(RegisterRequest(nome, email, senha, telefone, assinante))
    }
}
