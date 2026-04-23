package fr.koumare.gestion_de_livre.infrastructure.driving.web

import fr.koumare.gestion_de_livre.domain.model.Book
import fr.koumare.gestion_de_livre.domain.usecase.BookUseCase
import fr.koumare.gestion_de_livre.infrastructure.driving.web.dto.BookDTO
import fr.koumare.gestion_de_livre.infrastructure.driving.web.dto.BookReservationDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(private val service: BookUseCase) {

    @PostMapping
    fun addBook(@RequestBody bookDTO: BookDTO): ResponseEntity<Unit> {
        val book = Book(bookDTO.title.trim(), bookDTO.author.trim())
        service.addBook(book)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping
    fun listBooks(): List<BookDTO> {
        return service.listBooks().map { BookDTO.fromBook(it) }
    }

    @PostMapping("/reserve")
    fun reserveBook(@RequestBody reservationDTO: BookReservationDTO): ResponseEntity<BookDTO> {
        val reservedBook = service.reserveBook(reservationDTO.title.trim(), reservationDTO.author.trim())
        return if (reservedBook != null) {
            ResponseEntity.ok(BookDTO.fromBook(reservedBook))
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }
}