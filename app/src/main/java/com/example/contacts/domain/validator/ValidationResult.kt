package com.example.contacts.domain.validator

/** Kết quả validate: id chuỗi lỗi (null = hợp lệ). @JvmField để Java đọc thẳng `result.nameError`. */
data class ValidationResult(
    @JvmField val nameError: Int?,
    @JvmField val phoneError: Int?,
) {
    val isValid: Boolean get() = nameError == null && phoneError == null
}
