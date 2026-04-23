package fr.koumare.gestion_de_livre.infrastructure.driven.adapter

import fr.koumare.gestion_de_livre.domain.model.Book
import fr.koumare.gestion_de_livre.domain.port.BookPort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class BookDAO(
    private val jpaRepository: BookJpaRepository
) : BookPort {

    override fun save(book: Book): Book {
        val entity = BookEntity(
            id = book.id,
            title = book.title,
            author = book.author,
            isReserved = book.isReserved
        )
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findAll(): List<Book> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun findById(id: Long): Book? {
        return jpaRepository.findById(id).map { it.toDomain() }.orElse(null)
    }

    @Transactional
    override fun reserveBook(book: Book): Book {
        val entity = BookEntity(
            id = book.id,
            title = book.title,
            author = book.author,
            isReserved = book.isReserved
        )
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }
}