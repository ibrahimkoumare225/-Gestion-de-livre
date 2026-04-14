package fr.koumare.gestion_de_livre.domain.usecase

import Book
import fr.koumare.gestion_de_livre.domain.port.BookRepository

class BookService(private val repo: BookRepository) {

    fun addBook(book: Book) {
        if (book.title.isBlank() || book.author.isBlank()) {
            throw IllegalArgumentException("Invalid book")
        }
        repo.save(book)
    }

    fun listBooks(): List<Book> {
        return repo.findAll().sortedBy { it.title }
    }
}