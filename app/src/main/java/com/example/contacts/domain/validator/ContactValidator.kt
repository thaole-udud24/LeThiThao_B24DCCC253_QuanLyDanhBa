package com.example.contacts.domain.validator

import com.example.contacts.R
import com.example.contacts.data.ContactRepository
import com.example.contacts.util.Constants

class ContactValidator(private val repository: ContactRepository) {

    /** [editingId] khác null khi sửa liên hệ: bỏ qua chính nó lúc kiểm tra trùng SĐT. */
    @JvmOverloads
    fun validate(name: String, phone: String, editingId: String? = null): ValidationResult {
        val nameError = if (name.isBlank()) R.string.error_name_empty else null
        val p = phone.trim()
        val phoneError = when {
            !Constants.PHONE_REGEX.matches(p) -> R.string.error_phone_format
            repository.findByPhone(p)?.let { it.id != editingId } == true -> R.string.error_phone_duplicate
            else -> null
        }
        return ValidationResult(nameError, phoneError)
    }
}
