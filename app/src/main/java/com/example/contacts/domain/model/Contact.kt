package com.example.contacts.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Liên hệ. Parcelable để truyền qua Intent.
 * `id` đặt cuối + @JvmOverloads: Java gọi `new Contact(name, phone, ...)` mà không cần tự sinh id.
 */
@Parcelize
data class Contact @JvmOverloads constructor(
    val name: String,
    val phone: String,
    val group: ContactGroup = ContactGroup.OTHER,
    val gender: Gender = Gender.MALE,
    val favorite: Boolean = false,
    val note: String = "",
    val id: String = UUID.randomUUID().toString(),
) : Parcelable
