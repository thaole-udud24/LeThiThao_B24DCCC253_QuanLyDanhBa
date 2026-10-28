package com.example.contacts

import com.example.contacts.data.InMemoryContactRepository
import com.example.contacts.domain.model.Contact
import com.example.contacts.domain.model.ContactGroup
import com.example.contacts.domain.model.Gender
import com.example.contacts.domain.query.ContactFilter
import com.example.contacts.domain.query.SortOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class InMemoryContactRepositoryTest {
    private fun sample() = listOf(
        Contact("Nguyễn Văn An", "0901234567", ContactGroup.FRIENDS, Gender.MALE, false),
        Contact("Trần Thị Bình", "0912345678", ContactGroup.FAMILY, Gender.FEMALE, true),
        Contact("Lê Minh Châu", "0987654321", ContactGroup.WORK, Gender.MALE, false),
    )

    @Test
    fun startsEmpty() {
        val repo = InMemoryContactRepository()
        assertEquals(0, repo.stats().total)
        assertEquals(0, repo.stats().favorites)
        assertEquals(emptyList<Contact>(), repo.getAll())
    }

    @Test
    fun statsCountSeededContacts() {
        val repo = InMemoryContactRepository(sample())
        assertEquals(3, repo.stats().total)
        assertEquals(1, repo.stats().favorites)
        assertNotNull(repo.findByPhone("0912345678"))
    }

    @Test
    fun addThenDeleteUpdatesStats() {
        val repo = InMemoryContactRepository(emptyList())
        val c = Contact("An", "0901234567", favorite = true)
        repo.add(c)
        assertEquals(1, repo.stats().total)
        assertEquals(1, repo.stats().favorites)
        assertEquals(c, repo.delete(c.id))
        assertEquals(0, repo.stats().total)
        assertNull(repo.delete(c.id))
    }

    @Test
    fun toggleFavoriteFlipsAndUpdatesStats() {
        val repo = InMemoryContactRepository(emptyList())
        val c = Contact("An", "0901234567")
        repo.add(c)
        assertEquals(true, repo.toggleFavorite(c.id)?.favorite)
        assertEquals(1, repo.stats().favorites)
        assertEquals(false, repo.toggleFavorite(c.id)?.favorite)
    }

    @Test
    fun updateReplacesById() {
        val repo = InMemoryContactRepository(emptyList())
        val c = Contact("An", "0901234567")
        repo.add(c)
        repo.update(c.copy(name = "Anh"))
        assertEquals("Anh", repo.findById(c.id)?.name)
    }

    @Test
    fun filterSearchGroupAndSort() {
        val list = sample()
        assertEquals(1, ContactFilter(query = "bình").apply(list).size)
        assertEquals(1, ContactFilter(query = "0987").apply(list).size)
        assertEquals(1, ContactFilter(group = ContactGroup.WORK).apply(list).size)
        assertEquals("Trần Thị Bình", ContactFilter(sort = SortOrder.FAVORITE_FIRST).apply(list).first().name)
    }
}
