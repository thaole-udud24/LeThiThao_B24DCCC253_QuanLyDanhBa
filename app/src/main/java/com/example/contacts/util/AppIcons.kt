package com.example.contacts.util

import android.content.Context
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.example.contacts.R
import com.example.contacts.domain.model.ContactGroup
import com.google.android.material.textfield.TextInputLayout
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.iconics.utils.sizeDp

/** Danh mục icon của app. Đổi thư viện icon = sửa duy nhất file này. */
object AppIcon {
    @JvmField val LOGO: IIcon = GoogleMaterial.Icon.gmd_contacts
    @JvmField val USER: IIcon = GoogleMaterial.Icon.gmd_person
    @JvmField val LOCK: IIcon = GoogleMaterial.Icon.gmd_lock_outline
    @JvmField val EYE: IIcon = GoogleMaterial.Icon.gmd_visibility
    @JvmField val EYE_OFF: IIcon = GoogleMaterial.Icon.gmd_visibility_off
    @JvmField val SEARCH: IIcon = GoogleMaterial.Icon.gmd_search
    @JvmField val SORT: IIcon = GoogleMaterial.Icon.gmd_sort
    @JvmField val LOGOUT: IIcon = GoogleMaterial.Icon.gmd_exit_to_app
    @JvmField val ADD: IIcon = GoogleMaterial.Icon.gmd_add
    @JvmField val BACK: IIcon = GoogleMaterial.Icon.gmd_arrow_back
    @JvmField val EDIT: IIcon = GoogleMaterial.Icon.gmd_edit
    @JvmField val CALL: IIcon = GoogleMaterial.Icon.gmd_phone
    @JvmField val SMS: IIcon = GoogleMaterial.Icon.gmd_chat
    @JvmField val STAR: IIcon = GoogleMaterial.Icon.gmd_star
    @JvmField val STAR_OUTLINE: IIcon = GoogleMaterial.Icon.gmd_star_border
    @JvmField val DELETE: IIcon = GoogleMaterial.Icon.gmd_delete
    @JvmField val GROUP: IIcon = GoogleMaterial.Icon.gmd_group
    @JvmField val GENDER: IIcon = GoogleMaterial.Icon.gmd_wc
    @JvmField val NOTE: IIcon = GoogleMaterial.Icon.gmd_notes
    @JvmField val EMPTY: IIcon = GoogleMaterial.Icon.gmd_people
    @JvmField val ARROW_DOWN: IIcon = GoogleMaterial.Icon.gmd_keyboard_arrow_down
    @JvmField val CHEVRON: IIcon = GoogleMaterial.Icon.gmd_chevron_right
    @JvmField val HOME: IIcon = GoogleMaterial.Icon.gmd_home
    @JvmField val WORK: IIcon = GoogleMaterial.Icon.gmd_work
    @JvmField val MORE: IIcon = GoogleMaterial.Icon.gmd_more_horiz
    @JvmField val SAVE: IIcon = GoogleMaterial.Icon.gmd_save

    @JvmStatic
    fun forGroup(group: ContactGroup): IIcon = when (group) {
        ContactGroup.FAMILY -> HOME
        ContactGroup.FRIENDS -> GROUP
        ContactGroup.WORK -> WORK
        ContactGroup.OTHER -> MORE
    }
}

/** Tiện ích gắn icon (vẽ bằng font vector nên sắc nét ở mọi mật độ điểm ảnh). */
object AppIcons {
    @JvmStatic
    @JvmOverloads
    fun drawable(
        context: Context,
        icon: IIcon,
        @ColorRes color: Int = R.color.ink,
        size: Int = 24,
    ): IconicsDrawable = IconicsDrawable(context, icon).apply {
        colorInt = ContextCompat.getColor(context, color)
        sizeDp = size
    }

    @JvmStatic
    @JvmOverloads
    fun set(view: ImageView, icon: IIcon, @ColorRes color: Int = R.color.ink, size: Int = 24) {
        view.setImageDrawable(drawable(view.context, icon, color, size))
    }

    @JvmStatic
    @JvmOverloads
    fun setStart(layout: TextInputLayout, icon: IIcon, @ColorRes color: Int = R.color.muted) {
        layout.startIconDrawable = drawable(layout.context, icon, color)
        layout.setStartIconTintList(null) // giữ nguyên màu icon, không để Material nhuộm lại
    }

    @JvmStatic
    @JvmOverloads
    fun setEnd(layout: TextInputLayout, icon: IIcon, @ColorRes color: Int = R.color.muted) {
        layout.endIconDrawable = drawable(layout.context, icon, color)
        layout.setEndIconTintList(null)
    }
}
