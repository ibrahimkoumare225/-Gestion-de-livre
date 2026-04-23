package fr.koumare.gestion_de_livre.infrastructure.driven.adapter

import fr.koumare.gestion_de_livre.domain.model.Book
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "books")
class BookEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val author: String,

    @Column(nullable = false)
    val isReserved: Boolean = false,
) {
    constructor() : this(null, "", "", false)

    fun toDomain(): Book {
        return Book(title, author, isReserved)
    }
}