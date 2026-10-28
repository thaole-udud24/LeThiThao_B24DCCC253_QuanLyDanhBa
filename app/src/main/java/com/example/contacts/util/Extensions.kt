package com.example.contacts.util

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.contacts.R

/** Java dùng qua `ExtensionsKt.xxx(...)`. */

/** Chữ cái đầu của tên, in hoa, dùng làm avatar. */
fun String.avatarLetter(): String = trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"

/** ACTION_DIAL chỉ mở bàn phím quay số nên không cần quyền CALL_PHONE. */
fun String.toDialIntent(): Intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$this"))

fun String.toSmsIntent(): Intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$this"))

fun Context.startSafely(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, R.string.error_no_app, Toast.LENGTH_SHORT).show()
    }
}

fun Context.copyToClipboard(label: String, text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
}

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.tintBackground(@ColorRes colorRes: Int) {
    ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
}

/** Cho phép Java truyền lambda `text -> ...`. */
fun interface TextConsumer {
    fun accept(text: String)
}

fun TextView.onTextChanged(listener: TextConsumer) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) = listener.accept(s?.toString().orEmpty())
    })
}

/** Edge-to-edge: cộng thêm padding bằng kích thước status bar / nav bar / notch / bàn phím. */
@JvmOverloads
fun View.applySystemBarsPadding(includeIme: Boolean = true) {
    val left = paddingLeft
    val top = paddingTop
    val right = paddingRight
    val bottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        var types = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        if (includeIme) types = types or WindowInsetsCompat.Type.ime()
        val bars = insets.getInsets(types)
        v.setPadding(left + bars.left, top + bars.top, right + bars.right, bottom + bars.bottom)
        insets
    }
}
