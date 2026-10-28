package com.example.contacts.domain.model

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.example.contacts.R

/**
 * Nhóm liên hệ: nhãn, màu nền nhạt ([colorRes]) và màu chữ/icon đậm ([accentRes]).
 * Thêm nhóm = thêm 1 dòng.
 */
enum class ContactGroup(
    @StringRes val labelRes: Int,
    @ColorRes val colorRes: Int,
    @ColorRes val accentRes: Int,
) {
    FAMILY(R.string.group_family, R.color.family_soft, R.color.family_accent),
    FRIENDS(R.string.group_friends, R.color.friends_soft, R.color.friends_accent),
    WORK(R.string.group_work, R.color.work_soft, R.color.work_accent),
    OTHER(R.string.group_other, R.color.other_soft, R.color.other_accent),
}
