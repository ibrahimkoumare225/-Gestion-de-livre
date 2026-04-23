package fr.koumare.gestion_de_livre.domain.port

import fr.koumare.gestion_de_livre.domain.model.Book

interface BookPort {
    fun save(book: Book)
    fun findAll(): List<Book>
    fun reserveBook(title: String, author: String): Book?
}