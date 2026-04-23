package fr.koumare.gestion_de_livre.domain.usecase

import fr.koumare.gestion_de_livre.domain.model.Book
import fr.koumare.gestion_de_livre.domain.port.BookPort

class BookNotFoundException(id: Long) : RuntimeException("Book with id $id not found")
class BookAlreadyReservedException(id: Long) : RuntimeException("Book with id $id is already reserved")

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
}