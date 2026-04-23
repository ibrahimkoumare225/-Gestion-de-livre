package fr.koumare.gestion_de_livre.infrastructure.driven.adapter

import fr.koumare.gestion_de_livre.domain.model.Book
import fr.koumare.gestion_de_livre.domain.port.BookPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class BookDAO(
    private val jpaRepository: BookJpaRepository
) : BookPort {

    override fun save(book: Book) {
        val entity = BookEntity(title = book.title, author = book.author, isReserved = book.isReserved)
        jpaRepository.save(entity)
    }

    override fun findAll(): List<Book> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    @Transactional
    override fun reserveBook(title: String, author: String): Book? {
        val rowsUpdated = jpaRepository.reserveBook(title, author)
        return if (rowsUpdated > 0) {
            jpaRepository.findByTitleAndAuthor(title, author)?.toDomain()
        } else {
            null
        }
    }
}