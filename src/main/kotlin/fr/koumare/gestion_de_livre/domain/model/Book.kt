package fr.koumare.gestion_de_livre.domain.model

data class Book(
    val id: Long? = null,
    val title: String,
    val author: String,
    val isReserved: Boolean = false
) {
    init {
        require(title.isNotBlank()) { "Title must not be blank" }
        require(author.isNotBlank()) { "Author must not be blank" }
    }

    val isAvailable: Boolean
        get() = !isReserved

    fun reserve(): Book {
        require(!isReserved) { "Book is already reserved" }
        return copy(isReserved = true)
    }
}