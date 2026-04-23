package fr.koumare.gestion_de_livre.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BookTest {

    @Test
    fun `should create book with valid title and author`() {
        val book = Book(title = "Clean Code", author = "Robert C. Martin")

        assertEquals("Clean Code", book.title)
        assertEquals("Robert C. Martin", book.author)
        assertEquals(null, book.id)
        assertFalse(book.isReserved)
        assertTrue(book.isAvailable)
    }

    @Test
    fun `should create reserved book`() {
        val book = Book(title = "Clean Code", author = "Robert C. Martin", isReserved = true)

        assertEquals("Clean Code", book.title)
        assertEquals("Robert C. Martin", book.author)
        assertTrue(book.isReserved)
        assertFalse(book.isAvailable)
    }

    @Test
    fun `should reserve available book`() {
        val book = Book(id = 1, title = "Clean Code", author = "Robert C. Martin", isReserved = false)
        val reservedBook = book.reserve()

        assertEquals(1, reservedBook.id)
        assertEquals("Clean Code", reservedBook.title)
        assertEquals("Robert C. Martin", reservedBook.author)
        assertTrue(reservedBook.isReserved)
        assertFalse(reservedBook.isAvailable)
    }

    @Test
    fun `should throw exception when reserving already reserved book`() {
        val book = Book(id = 1, title = "Clean Code", author = "Robert C. Martin", isReserved = true)

        assertFailsWith<IllegalArgumentException> {
            book.reserve()
        }
    }

    @Test
    fun `should throw exception when title is blank`() {
        assertFailsWith<IllegalArgumentException> {
            Book(1L, "", "Author", false)
        }
    }

    @Test
    fun `should throw exception when author is blank`() {
        assertFailsWith<IllegalArgumentException> {
            Book(1L, "Title", "", false)
        }
    }
}