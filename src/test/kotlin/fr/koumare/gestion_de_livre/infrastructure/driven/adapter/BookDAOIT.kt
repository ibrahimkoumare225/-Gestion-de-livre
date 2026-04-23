package fr.koumare.gestion_de_livre.infrastructure.driven.adapter

import fr.koumare.gestion_de_livre.domain.model.Book
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Tag("integration")
class BookDAOIT {

    private val jpaRepository = mockk<BookJpaRepository>()

    @Test
    fun `should save book to repository`() {
        val dao = BookDAO(jpaRepository)
        val book = Book("Test Book", "Test Author")
        val entity = BookEntity(title = "Test Book", author = "Test Author")

        every { jpaRepository.save(any()) } returns entity

        dao.save(book)

        verify { jpaRepository.save(match { it.title == "Test Book" && it.author == "Test Author" }) }
    }

    @Test
    fun `should find all books from repository`() {
        val dao = BookDAO(jpaRepository)
        val entities = listOf(
            BookEntity(id = 1, title = "Book A", author = "Author A"),
            BookEntity(id = 2, title = "Book B", author = "Author B")
        )
        every { jpaRepository.findAll() } returns entities

        val result = dao.findAll()

        assertEquals(2, result.size)
        assertEquals("Book A", result[0].title)
        assertEquals("Author A", result[0].author)
        assertEquals("Book B", result[1].title)
        assertEquals("Author B", result[1].author)
    }

    @Test
    fun `should reserve book successfully`() {
        val dao = BookDAO(jpaRepository)
        val reservedEntity = BookEntity(id = 1, title = "Test Book", author = "Test Author", isReserved = true)

        every { jpaRepository.reserveBook("Test Book", "Test Author") } returns 1
        every { jpaRepository.findByTitleAndAuthor("Test Book", "Test Author") } returns reservedEntity

        val result = dao.reserveBook("Test Book", "Test Author")

        assertEquals("Test Book", result?.title)
        assertEquals("Test Author", result?.author)
        assertEquals(true, result?.isReserved)
        verify { jpaRepository.reserveBook("Test Book", "Test Author") }
        verify { jpaRepository.findByTitleAndAuthor("Test Book", "Test Author") }
    }

    @Test
    fun `should return null when book reservation fails`() {
        val dao = BookDAO(jpaRepository)

        every { jpaRepository.reserveBook("Test Book", "Test Author") } returns 0

        val result = dao.reserveBook("Test Book", "Test Author")

        assertEquals(null, result)
        verify { jpaRepository.reserveBook("Test Book", "Test Author") }
    }
}