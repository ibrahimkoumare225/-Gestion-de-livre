package fr.koumare.gestion_de_livre.domain.usecase

import fr.koumare.gestion_de_livre.domain.model.Book
import fr.koumare.gestion_de_livre.domain.port.BookPort

class BookUseCase(private val repo: BookPort) {

    fun addBook(book: Book) {
        if (book.title.isBlank() || book.author.isBlank()) {
            throw IllegalArgumentException("Invalid book")
        }
        repo.save(book)
    }

    fun listBooks(): List<Book> {
        return repo.findAll().sortedBy { it.title }
    }

    fun reserveBook(title: String, author: String): Book? {
        if (title.isBlank() || author.isBlank()) {
            throw IllegalArgumentException("Title and author must not be blank")
        }

        // Check if book exists and is available
        val book = repo.findAll().find { it.title == title && it.author == author }
            ?: throw IllegalArgumentException("Book not found")

        if (book.isReserved) {
            throw IllegalArgumentException("Book is already reserved")
        }

        return repo.reserveBook(title, author)
    }
}