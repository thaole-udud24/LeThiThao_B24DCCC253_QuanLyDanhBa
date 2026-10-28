package com.example.contacts.util

object Constants {
    const val EXTRA_CONTACT = "extra_contact"
    const val EXTRA_DELETED = "extra_deleted"
    const val EXTRA_USERNAME = "extra_username"
    const val NAME_MAX_LENGTH = 50
    const val NOTE_MAX_LENGTH = 200

    /** 10 chữ số, bắt đầu bằng 0. */
    val PHONE_REGEX = Regex("^0\\d{9}$")
}
