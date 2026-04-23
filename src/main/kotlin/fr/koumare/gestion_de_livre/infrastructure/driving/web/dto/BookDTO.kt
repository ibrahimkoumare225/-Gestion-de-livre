package fr.koumare.gestion_de_livre.infrastructure.driving.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import fr.koumare.gestion_de_livre.domain.model.Book

data class BookDTO(
    val title: String,
    val author: String,
    @get:JsonProperty("isAvailable")
    val isAvailable: Boolean,
) {
    companion object {
        fun fromBook(book: Book): BookDTO = BookDTO(book.title, book.author, book.isAvailable)
    }
}