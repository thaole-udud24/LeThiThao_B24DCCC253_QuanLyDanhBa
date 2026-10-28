package com.example.contacts.data

import com.example.contacts.domain.model.Contact
import com.example.contacts.domain.model.ContactStats

/** Lưu trong ArrayList (mất khi tắt app). Mặc định rỗng; [seed] dùng cho test. */
class InMemoryContactRepository @JvmOverloads constructor(
    seed: List<Contact> = emptyList(),
) : ContactRepository {

    private val items = ArrayList(seed)

    @Synchronized
    override fun getAll(): List<Contact> = items.toList()

    @Synchronized
    override fun findById(id: String): Contact? = items.firstOrNull { it.id == id }

    @Synchronized
    override fun findByPhone(phone: String): Contact? = items.firstOrNull { it.phone == phone }

    @Synchronized
    override fun add(contact: Contact) {
        items.add(contact)
    }

    @Synchronized
    override fun update(contact: Contact) {
        val index = items.indexOfFirst { it.id == contact.id }
        if (index >= 0) items[index] = contact
    }

    @Synchronized
    override fun delete(id: String): Contact? {
        val index = items.indexOfFirst { it.id == id }
        return if (index >= 0) items.removeAt(index) else null
    }

    @Synchronized
    override fun toggleFavorite(id: String): Contact? {
        val index = items.indexOfFirst { it.id == id }
        if (index < 0) return null
        return items[index].copy(favorite = !items[index].favorite).also { items[index] = it }
    }

    @Synchronized
    override fun stats() = ContactStats(items.size, items.count { it.favorite })
}
