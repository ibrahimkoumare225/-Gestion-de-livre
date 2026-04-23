package fr.koumare.gestion_de_livre.domain.usecase

import fr.koumare.gestion_de_livre.domain.model.Book
import fr.koumare.gestion_de_livre.domain.port.BookPort

class BookNotFoundException private constructor(message: String) : RuntimeException(message) {
    constructor(id: Long) : this("Book with id $id not found")
    constructor(title: String, author: String) : this("Book with title '$title' and author '$author' not found")
}

class BookAlreadyReservedException private constructor(message: String) : RuntimeException(message) {
    constructor(id: Long) : this("Book with id $id is already reserved")
    constructor(title: String, author: String) : this("Book with title '$title' and author '$author' is already reserved")
}

class BookUseCase(private val repo: BookPort) {

    fun addBook(book: Book): Book {
        if (book.title.isBlank() || book.author.isBlank()) {
            throw IllegalArgumentException("Invalid book")
        }
        return repo.save(book)
    }

    fun listBooks(): List<Book> {
        return repo.findAll().sortedBy { it.title.lowercase() }
    }

    fun reserveBook(id: Long): Book {
        val book = repo.findById(id) ?: throw BookNotFoundException(id)
        if (book.isReserved) {
            throw BookAlreadyReservedException(id)
        }
        val reservedBook = book.reserve()
        return repo.reserveBook(reservedBook)
    }

    fun reserveBook(title: String, author: String): Book {
        val book = repo.findByTitleAndAuthor(title, author)
            ?: throw BookNotFoundException(title, author)
        if (book.isReserved) {
            throw BookAlreadyReservedException(title, author)
        }
        val reservedBook = book.reserve()
        return repo.reserveBook(reservedBook)
    }
}