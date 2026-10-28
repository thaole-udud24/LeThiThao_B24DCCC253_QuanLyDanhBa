package com.example.contacts

import com.example.contacts.data.InMemoryContactRepository
import com.example.contacts.domain.model.Contact
import com.example.contacts.domain.validator.ContactValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ContactValidatorTest {
    private val existing = Contact("An", "0901234567")
    private lateinit var validator: ContactValidator

    @Before
    fun setUp() {
        validator = ContactValidator(InMemoryContactRepository(listOf(existing)))
    }

    @Test
    fun blankName() {
        assertEquals(R.string.error_name_empty, validator.validate("   ", "0912345678").nameError)
    }

    @Test
    fun phoneTooShort() {
        assertEquals(R.string.error_phone_format, validator.validate("B", "091234567").phoneError)
    }

    @Test
    fun phoneNotStartingWithZero() {
        assertEquals(R.string.error_phone_format, validator.validate("B", "1912345678").phoneError)
    }

    @Test
    fun phoneWithLetters() {
        assertEquals(R.string.error_phone_format, validator.validate("B", "09a1234567").phoneError)
    }

    @Test
    fun duplicatePhone() {
        assertEquals(R.string.error_phone_duplicate, validator.validate("B", "0901234567").phoneError)
    }

    @Test
    fun duplicateIgnoredForSameContactWhenEditing() {
        assertNull(validator.validate("An", "0901234567", existing.id).phoneError)
    }

    @Test
    fun validInput() {
        assertTrue(validator.validate("Bình", "0912345678").isValid)
    }
}
