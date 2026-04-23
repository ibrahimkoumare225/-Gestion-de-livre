package fr.koumare.gestion_de_livre.infrastructure.driving.web

import fr.koumare.gestion_de_livre.domain.usecase.BookAlreadyReservedException
import fr.koumare.gestion_de_livre.domain.usecase.BookNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class BookControllerAdvice {

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(exception: IllegalArgumentException): Map<String, String> {
        return mapOf("message" to (exception.message ?: "Bad request"))
    }

    @ExceptionHandler(BookNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(exception: BookNotFoundException): Map<String, String> {
        return mapOf("message" to (exception.message ?: "Book not found"))
    }

    @ExceptionHandler(BookAlreadyReservedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleConflict(exception: BookAlreadyReservedException): Map<String, String> {
        return mapOf("message" to (exception.message ?: "Book is already reserved"))
    }
}