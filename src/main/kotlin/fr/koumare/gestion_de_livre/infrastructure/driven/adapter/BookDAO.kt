package fr.koumare.gestion_de_livre.infrastructure.driven.adapter

import fr.koumare.gestion_de_livre.domain.model.Book
import fr.koumare.gestion_de_livre.domain.port.BookPort
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookDAO(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : BookPort {

    private val bookRowMapper = RowMapper { rs, _ ->
        Book(
            id = rs.getLong("id").takeIf { !rs.wasNull() },
            title = rs.getString("title"),
            author = rs.getString("author"),
            isReserved = rs.getBoolean("is_reserved")
        )
    }

    override fun save(book: Book): Book {
        val sql = """
            INSERT INTO books (title, author, is_reserved)
            VALUES (:title, :author, :isReserved)
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("title", book.title)
            .addValue("author", book.author)
            .addValue("isReserved", book.isReserved)

        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(sql, params, keyHolder, arrayOf("id"))

        val generatedId = keyHolder.key?.toLong()
            ?: throw IllegalStateException("Failed to generate book id")

        return book.copy(id = generatedId)
    }

    override fun findAll(): List<Book> {
        val sql = "SELECT id, title, author, is_reserved FROM books"
        return jdbcTemplate.query(sql, MapSqlParameterSource(), bookRowMapper)
    }

    override fun findById(id: Long): Book? {
        val sql = "SELECT id, title, author, is_reserved FROM books WHERE id = :id"
        return jdbcTemplate.query(sql, MapSqlParameterSource("id", id), bookRowMapper).firstOrNull()
    }

    override fun findByTitleAndAuthor(title: String, author: String): Book? {
        val sql = "SELECT id, title, author, is_reserved FROM books WHERE title = :title AND author = :author"
        return jdbcTemplate.query(
            sql,
            MapSqlParameterSource()
                .addValue("title", title)
                .addValue("author", author),
            bookRowMapper
        ).firstOrNull()
    }

    @Transactional
    override fun reserveBook(book: Book): Book {
        val id = book.id ?: throw IllegalArgumentException("Book id must not be null")

        val sql = "UPDATE books SET is_reserved = true WHERE id = :id AND is_reserved = false"
        jdbcTemplate.update(sql, MapSqlParameterSource("id", id))

        return book
    }
}
