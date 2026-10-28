package com.example.contacts.domain.model

import androidx.annotation.StringRes
import com.example.contacts.R

enum class Gender(@StringRes val labelRes: Int) {
    MALE(R.string.gender_male),
    FEMALE(R.string.gender_female),
    OTHER(R.string.gender_other),
}
