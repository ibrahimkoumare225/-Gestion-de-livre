package fr.koumare.gestion_de_livre.domain.usecase

import Book
import fr.koumare.gestion_de_livre.domain.port.BookRepository

class FakeBookRepository : BookRepository {
    private val books = mutableListOf<Book>()

    override fun save(book: Book) {
        books.add(book)
    }

    override fun findAll(): List<Book> {
        return books
    }
}