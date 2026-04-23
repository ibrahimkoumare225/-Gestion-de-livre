package fr.koumare.gestion_de_livre.infrastructure.driving.web

import fr.koumare.gestion_de_livre.domain.model.Book
import fr.koumare.gestion_de_livre.domain.usecase.BookUseCase
import fr.koumare.gestion_de_livre.infrastructure.driving.web.dto.BookDTO
import fr.koumare.gestion_de_livre.infrastructure.driving.web.dto.toDomain
import fr.koumare.gestion_de_livre.infrastructure.driving.web.dto.toDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(private val service: BookUseCase) {

    @PostMapping
    fun addBook(@RequestBody bookDTO: BookDTO): ResponseEntity<BookDTO> {
        val book = Book(title = bookDTO.title.trim(), author = bookDTO.author.trim())
        val savedBook = service.addBook(book)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook.toDTO())
    }

    @GetMapping
    fun listBooks(): List<BookDTO> {
        return service.listBooks().map { it.toDTO() }
    }

    @PostMapping("/{id}/reserve")
    fun reserveBook(@PathVariable id: Long): BookDTO {
        val reservedBook = service.reserveBook(id)
        return reservedBook.toDTO()
    }
}