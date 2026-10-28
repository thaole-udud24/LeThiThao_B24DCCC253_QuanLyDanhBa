package com.example.contacts

import com.example.contacts.auth.AuthResult
import com.example.contacts.auth.FixedAuthService
import org.junit.Assert.assertEquals
import org.junit.Test

class FixedAuthServiceTest {
    private val auth = FixedAuthService()

    @Test
    fun correctCredentials() = assertEquals(AuthResult.SUCCESS, auth.login("admin", "123456"))

    @Test
    fun wrongUsername() = assertEquals(AuthResult.INVALID_CREDENTIALS, auth.login("root", "123456"))

    @Test
    fun wrongPassword() = assertEquals(AuthResult.INVALID_CREDENTIALS, auth.login("admin", "000000"))

    @Test
    fun emptyInput() {
        assertEquals(AuthResult.EMPTY_INPUT, auth.login("", "123456"))
        assertEquals(AuthResult.EMPTY_INPUT, auth.login("admin", ""))
    }
}
