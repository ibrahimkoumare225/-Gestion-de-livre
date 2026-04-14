package fr.koumare.gestion_de_livre.domain.usecase

import Book
import kotlin.test.Test
import kotlin.test.assertEquals

class BookServiceTest {

    @Test
    fun `should add book`() {
        val repo = FakeBookRepository()
        val service = BookService(repo)

        service.addBook(Book("A", "Author"))

        assertEquals(1, repo.findAll().size)
    }

    @Test
    fun `should sort books`() {
        val repo = FakeBookRepository()
        val service = BookService(repo)

        service.addBook(Book("B", "A"))
        service.addBook(Book("A", "A"))

        val result = service.listBooks()

        assertEquals("A", result[0].title)
    }
}