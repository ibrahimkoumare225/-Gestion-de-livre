package fr.koumare.gestion_de_livre.domain.port

import fr.koumare.gestion_de_livre.domain.model.Book

interface BookPort {
    fun findAll(): List<Book>
    fun save(book: Book): Book
    fun findById(id: Long): Book?
    fun findByTitleAndAuthor(title: String, author: String): Book?
    fun reserveBook(book: Book): Book
}