package fr.koumare.gestion_de_livre.domain.usecase

import fr.koumare.gestion_de_livre.domain.model.Book
import fr.koumare.gestion_de_livre.domain.port.BookPort

class FakeBookPort : BookPort {
    private val books = mutableListOf<Book>()

    override fun save(book: Book): Book {
        books.add(book)
        return book
    }

    override fun findAll(): List<Book> {
        return books
    }

    override fun findById(id: Long): Book? {
        return books.firstOrNull { it.id == id }
    }

    override fun reserveBook(book: Book): Book {
        val index = books.indexOfFirst { it.id == book.id }
        if (index == -1) {
            throw BookNotFoundException(book.id ?: -1L)
        }

        books[index] = book
        return book
    }
}