package com.example.contacts.domain.query

import com.example.contacts.domain.model.Contact
import com.example.contacts.domain.model.ContactGroup

enum class SortOrder { NAME_ASC, FAVORITE_FIRST }

/** Điều kiện tìm kiếm / lọc / sắp xếp danh sách; thuần Kotlin nên test không cần Android. */
data class ContactFilter(
    val query: String = "",
    val group: ContactGroup? = null,
    val favoritesOnly: Boolean = false,
    val sort: SortOrder = SortOrder.NAME_ASC,
) {
    fun apply(contacts: List<Contact>): List<Contact> {
        val q = query.trim()
        val matched = contacts.filter {
            (group == null || it.group == group) &&
                (!favoritesOnly || it.favorite) &&
                (q.isEmpty() || it.name.contains(q, ignoreCase = true) || it.phone.contains(q))
        }
        // shortcut: so sánh theo lowercase, chưa dùng Collator tiếng Việt; nâng cấp khi cần xếp đúng dấu
        val byName = compareBy<Contact> { it.name.lowercase() }
        return when (sort) {
            SortOrder.NAME_ASC -> matched.sortedWith(byName)
            SortOrder.FAVORITE_FIRST -> matched.sortedWith(compareByDescending<Contact> { it.favorite }.then(byName))
        }
    }
}
