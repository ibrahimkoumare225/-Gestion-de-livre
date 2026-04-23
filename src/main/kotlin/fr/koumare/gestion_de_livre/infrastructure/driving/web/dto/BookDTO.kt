package fr.koumare.gestion_de_livre.infrastructure.driving.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import fr.koumare.gestion_de_livre.domain.model.Book

data class BookDTO(
    val id: Long? = null,
    val title: String,
    val author: String,
    @get:JsonProperty("isAvailable")
    val isAvailable: Boolean,
)

fun BookDTO.toDomain(): Book = Book(
    id = this.id,
    title = this.title,
    author = this.author,
    isReserved = !this.isAvailable
)

fun Book.toDTO(): BookDTO = BookDTO(
    id = this.id,
    title = this.title,
    author = this.author,
    isAvailable = this.isAvailable
)