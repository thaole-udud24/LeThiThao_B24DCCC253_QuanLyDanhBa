package com.example.contacts.data

import com.example.contacts.auth.AuthService
import com.example.contacts.auth.FixedAuthService
import com.example.contacts.domain.validator.ContactValidator

/** Điểm duy nhất tạo dependency: thay InMemory bằng Room / FixedAuth bằng API chỉ sửa ở đây. */
object ServiceLocator {
    private val repository: ContactRepository by lazy { InMemoryContactRepository() }
    private val auth: AuthService by lazy { FixedAuthService() }

    @JvmStatic
    fun contactRepository(): ContactRepository = repository

    @JvmStatic
    fun authService(): AuthService = auth

    @JvmStatic
    fun contactValidator(): ContactValidator = ContactValidator(repository)
}
