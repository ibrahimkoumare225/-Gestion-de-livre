package fr.koumare.gestion_de_livre.infrastructure.driven.adapter

import fr.koumare.gestion_de_livre.domain.model.Book
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder

@Tag("integration")
class BookDAOIT {

    private val jdbcTemplate = mockk<NamedParameterJdbcTemplate>()

    @Test
    fun `should save book and return it with id`() {
        val dao = BookDAO(jdbcTemplate)
        val book = Book(title = "Test Book", author = "Test Author")
        val keyHolderSlot = slot<GeneratedKeyHolder>()

        every {
            jdbcTemplate.update(
                any(),
                any<MapSqlParameterSource>(),
                capture(keyHolderSlot),
                any<Array<String>>()
            )
        } answers {
            keyHolderSlot.captured.keyList.add(mapOf("id" to 1))
            1
        }

        val result = dao.save(book)

        result.id shouldBe 1
        result.title shouldBe "Test Book"
        result.author shouldBe "Test Author"
        result.isReserved shouldBe false
    }

    @Test
    fun `should find book by id`() {
        val dao = BookDAO(jdbcTemplate)
        val book = Book(id = 1, title = "Test Book", author = "Test Author", isReserved = false)

        every {
            jdbcTemplate.query(
                any(),
                any<MapSqlParameterSource>(),
                any<RowMapper<Book>>()
            )
        } returns listOf(book)

        val result = dao.findById(1)

        result?.id shouldBe 1
        result?.title shouldBe "Test Book"
        result?.author shouldBe "Test Author"
        result?.isReserved shouldBe false
    }

    @Test
    fun `should return null when book not found by id`() {
        val dao = BookDAO(jdbcTemplate)

        every {
            jdbcTemplate.query(
                any(),
                any<MapSqlParameterSource>(),
                any<RowMapper<Book>>()
            )
        } returns emptyList()

        val result = dao.findById(1)

        result shouldBe null
    }

    @Test
    fun `should reserve book successfully`() {
        val dao = BookDAO(jdbcTemplate)
        val book = Book(id = 1, title = "Test Book", author = "Test Author", isReserved = true)

        every {
            jdbcTemplate.update(any(), any<MapSqlParameterSource>())
        } returns 1

        val result = dao.reserveBook(book)

        result.id shouldBe 1
        result.title shouldBe "Test Book"
        result.author shouldBe "Test Author"
        result.isReserved shouldBe true
    }
}
