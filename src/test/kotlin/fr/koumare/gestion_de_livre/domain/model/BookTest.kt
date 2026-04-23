package fr.koumare.gestion_de_livre.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BookTest {

    @Test
    fun `should create book with valid title and author`() {
        val book = Book("Clean Code", "Robert C. Martin")

        assertEquals("Clean Code", book.title)
        assertEquals("Robert C. Martin", book.author)
        assertFalse(book.isReserved)
        assertTrue(book.isAvailable)
    }

    @Test
    fun `should create reserved book`() {
        val book = Book("Clean Code", "Robert C. Martin", isReserved = true)

        assertEquals("Clean Code", book.title)
        assertEquals("Robert C. Martin", book.author)
        assertTrue(book.isReserved)
        assertFalse(book.isAvailable)
    }

    @Test
    fun `should throw exception when title is blank`() {
        assertFailsWith<IllegalArgumentException> {
            Book("", "Author")
        }
    }

    @Test
    fun `should throw exception when author is blank`() {
        assertFailsWith<IllegalArgumentException> {
            Book("Title", "")
        }
    }
}