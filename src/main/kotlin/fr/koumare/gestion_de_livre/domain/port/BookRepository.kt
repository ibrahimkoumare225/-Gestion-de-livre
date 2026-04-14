package fr.koumare.gestion_de_livre.domain.port

import Book

interface BookRepository {
    fun save(book: Book)
    fun findAll(): List<Book>
}