package com.example.contacts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contacts.data.ContactRepository
import com.example.contacts.data.ServiceLocator
import com.example.contacts.domain.model.Contact
import com.example.contacts.domain.model.ContactGroup
import com.example.contacts.domain.query.ContactFilter
import com.example.contacts.domain.query.SortOrder

/** Trạng thái màn hình danh sách: danh sách đã lọc + thống kê của TOÀN BỘ danh bạ. */
data class MainState(val items: List<Contact>, val total: Int, val favorites: Int)

class MainViewModel(
    private val repository: ContactRepository = ServiceLocator.contactRepository(),
) : ViewModel() {

    private val _state = MutableLiveData<MainState>()
    val state: LiveData<MainState> = _state

    private var filter = ContactFilter()

    init {
        refresh()
    }

    /** Đọc lại repository (gọi sau khi Add/Detail thay đổi dữ liệu) và cập nhật cả thống kê. */
    fun refresh() {
        val stats = repository.stats()
        _state.value = MainState(filter.apply(repository.getAll()), stats.total, stats.favorites)
    }

    fun setQuery(query: String) = update { it.copy(query = query) }

    fun setCategory(group: ContactGroup?, favoritesOnly: Boolean) =
        update { it.copy(group = group, favoritesOnly = favoritesOnly) }

    fun setSort(sort: SortOrder) = update { it.copy(sort = sort) }

    fun toggleFavorite(id: String) {
        repository.toggleFavorite(id)
        refresh()
    }

    fun delete(id: String): Contact? = repository.delete(id).also { refresh() }

    /** Hoàn tác xóa; bỏ qua nếu SĐT đã bị liên hệ khác dùng trong lúc đó. */
    fun restore(contact: Contact) {
        if (repository.findByPhone(contact.phone) == null) repository.add(contact)
        refresh()
    }

    private fun update(change: (ContactFilter) -> ContactFilter) {
        filter = change(filter)
        refresh()
    }
}
