package com.example.contacts.auth

enum class AuthResult { SUCCESS, EMPTY_INPUT, INVALID_CREDENTIALS }

interface AuthService {
    fun login(username: String, password: String): AuthResult
}
