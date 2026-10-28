package com.example.contacts.auth

/** Tài khoản hard-code theo đề: admin / 123456. */
class FixedAuthService : AuthService {
    override fun login(username: String, password: String): AuthResult = when {
        username.isBlank() || password.isEmpty() -> AuthResult.EMPTY_INPUT
        username.trim() == USERNAME && password == PASSWORD -> AuthResult.SUCCESS
        else -> AuthResult.INVALID_CREDENTIALS
    }

    private companion object {
        const val USERNAME = "admin"
        const val PASSWORD = "123456"
    }
}
