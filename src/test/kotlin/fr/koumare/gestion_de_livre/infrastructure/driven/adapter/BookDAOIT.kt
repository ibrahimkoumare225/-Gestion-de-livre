package fr.koumare.gestion_de_livre.infrastructure.driven.adapter

import fr.koumare.gestion_de_livre.domain.model.Book
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("integration")
class BookDAOIT {

    private val jpaRepository = mockk<BookJpaRepository>()

    @Test
    fun `should save book and return it with id`() {
        val dao = BookDAO(jpaRepository)
        val book = Book(title = "Test Book", author = "Test Author")
        val savedEntity = BookEntity(id = 1, title = "Test Book", author = "Test Author", isReserved = false)

        every { jpaRepository.save(any()) } returns savedEntity

        val result = dao.save(book)

        result.id shouldBe 1
        result.title shouldBe "Test Book"
        result.author shouldBe "Test Author"
        result.isReserved shouldBe false
    }

    @Test
    fun `should find book by id`() {
        val dao = BookDAO(jpaRepository)
        val entity = BookEntity(id = 1, title = "Test Book", author = "Test Author", isReserved = false)

        every { jpaRepository.findById(1) } returns java.util.Optional.of(entity)

        val result = dao.findById(1)

        result?.id shouldBe 1
        result?.title shouldBe "Test Book"
        result?.author shouldBe "Test Author"
        result?.isReserved shouldBe false
    }

    @Test
    fun `should return null when book not found by id`() {
        val dao = BookDAO(jpaRepository)

        every { jpaRepository.findById(1) } returns java.util.Optional.empty()

        val result = dao.findById(1)

        result shouldBe null
    }

    @Test
    fun `should reserve book successfully`() {
        val dao = BookDAO(jpaRepository)
        val book = Book(id = 1, title = "Test Book", author = "Test Author", isReserved = true)
        val savedEntity = BookEntity(id = 1, title = "Test Book", author = "Test Author", isReserved = true)

        every { jpaRepository.save(any()) } returns savedEntity

        val result = dao.reserveBook(book)

        result.id shouldBe 1
        result.title shouldBe "Test Book"
        result.author shouldBe "Test Author"
        result.isReserved shouldBe true
    }
}