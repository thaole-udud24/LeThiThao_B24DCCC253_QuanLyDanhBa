package com.example.contacts.data

import com.example.contacts.domain.model.Contact
import com.example.contacts.domain.model.ContactStats

/** Nơi lưu danh bạ. UI chỉ biết interface này; đổi sang Room chỉ cần viết bản cài đặt mới. */
interface ContactRepository {
    fun getAll(): List<Contact>
    fun findById(id: String): Contact?
    fun findByPhone(phone: String): Contact?
    fun add(contact: Contact)
    fun update(contact: Contact)

    /** Trả về liên hệ đã xóa (để hoàn tác) hoặc null nếu không tồn tại. */
    fun delete(id: String): Contact?

    /** Trả về liên hệ sau khi đổi trạng thái yêu thích, null nếu không tồn tại. */
    fun toggleFavorite(id: String): Contact?
    fun stats(): ContactStats
}
