package fr.koumare.gestion_de_livre.infrastructure.driving.web

import fr.koumare.gestion_de_livre.infrastructure.driving.web.dto.BookDTO
import fr.koumare.gestion_de_livre.infrastructure.driving.web.dto.BookReservationDTO
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookReservationIntegrationTest(
    @Autowired private val restTemplate: TestRestTemplate,
    @LocalServerPort private val port: Int
) {

    @Test
    fun `should reserve a book by title and author end-to-end`() {
        val bookRequest = BookDTO(title = "Clean Code", author = "Robert C. Martin", isAvailable = true)
        val createResponse = restTemplate.postForEntity("/books", bookRequest, BookDTO::class.java)

        assertEquals(201, createResponse.statusCodeValue)
        val createdBook = createResponse.body!!
        assertEquals("Clean Code", createdBook.title)
        assertEquals("Robert C. Martin", createdBook.author)
        assertEquals(true, createdBook.isAvailable)

        val reservationRequest = BookReservationDTO(title = "Clean Code", author = "Robert C. Martin")
        val reserveResponse = restTemplate.postForEntity("/books/reserve", reservationRequest, BookDTO::class.java)

        assertEquals(200, reserveResponse.statusCodeValue)
        val reservedBook = reserveResponse.body!!
        assertEquals("Clean Code", reservedBook.title)
        assertEquals("Robert C. Martin", reservedBook.author)
        assertEquals(false, reservedBook.isAvailable)
    }
}
