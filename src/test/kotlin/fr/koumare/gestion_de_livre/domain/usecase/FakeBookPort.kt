package fr.koumare.gestion_de_livre.domain.usecase

import fr.koumare.gestion_de_livre.domain.model.Book
import fr.koumare.gestion_de_livre.domain.port.BookPort

class FakeBookPort : BookPort {
    private val books = mutableListOf<Book>()

    override fun save(book: Book) {
        books.add(book)
    }

    override fun findAll(): List<Book> {
        return books
    }

    override fun reserveBook(title: String, author: String): Book? {
        val index = books.indexOfFirst { it.title == title && it.author == author && !it.isReserved }
        return if (index != -1) {
            val book = books[index]
            val reservedBook = book.copy(isReserved = true)
            books[index] = reservedBook
            reservedBook
        } else {
            null
        }
    }
}