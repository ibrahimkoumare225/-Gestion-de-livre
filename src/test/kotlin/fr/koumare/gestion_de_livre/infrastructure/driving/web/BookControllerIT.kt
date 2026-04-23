package fr.koumare.gestion_de_livre.infrastructure.driving.web

import fr.koumare.gestion_de_livre.domain.model.Book
import com.ninjasquad.springmockk.MockkBean
import fr.koumare.gestion_de_livre.domain.usecase.BookAlreadyReservedException
import fr.koumare.gestion_de_livre.domain.usecase.BookNotFoundException
import fr.koumare.gestion_de_livre.domain.usecase.BookUseCase
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.junit.jupiter.api.Tag

@WebMvcTest(BookController::class)
@Tag("integration")
class BookControllerIT(
    @Autowired val mockMvc: MockMvc,
    @MockkBean val bookUseCase: BookUseCase
) : StringSpec({

    "POST /books should create book and return it with id" {
        val bookDTO = """{"title":"Test Book","author":"Test Author","isAvailable":true}"""
        val expectedBook = Book(title = "Test Book", author = "Test Author")
        val savedBook = expectedBook.copy(id = 1)
        every { bookUseCase.addBook(expectedBook) } returns savedBook

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = bookDTO
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                json("""{"id":1,"title":"Test Book","author":"Test Author","isAvailable":true}""")
            }
        }
    }

    "GET /books should return all books sorted by title with availability" {
        val books = listOf(
            Book(id = 2, title = "Book B", author = "Author B", isReserved = false),
            Book(id = 1, title = "Book A", author = "Author A", isReserved = false),
            Book(id = 3, title = "Book C", author = "Author C", isReserved = true)
        )
        every { bookUseCase.listBooks() } returns books.sortedBy { it.title.lowercase() }

        mockMvc.get("/books") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                json(
                    """
                    [
                        {"id":1,"title":"Book A","author":"Author A","isAvailable":true},
                        {"id":2,"title":"Book B","author":"Author B","isAvailable":true},
                        {"id":3,"title":"Book C","author":"Author C","isAvailable":false}
                    ]
                    """.trimIndent()
                )
            }
        }
    }

    "GET /books should return empty list when no books" {
        every { bookUseCase.listBooks() } returns emptyList()

        mockMvc.get("/books") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { json("[]") }
        }
    }

    "POST /books/{id}/reserve should reserve a book successfully" {
        val bookId = 1L
        val book = Book(id = bookId, title = "Test Book", author = "Test Author", isReserved = false)
        val reservedBook = book.reserve()
        every { bookUseCase.reserveBook(bookId) } returns reservedBook

        mockMvc.post("/books/$bookId/reserve") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                json(
                    """
                    {"id":1,"title":"Test Book","author":"Test Author","isAvailable":false}
                    """.trimIndent()
                )
            }
        }
    }

    "POST /books/{id}/reserve should return 409 when book cannot be reserved" {
        val bookId = 1L
        every { bookUseCase.reserveBook(bookId) } throws BookAlreadyReservedException(bookId)

        mockMvc.post("/books/$bookId/reserve") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isConflict() }
        }
    }

    "POST /books/{id}/reserve should return 404 when book not found" {
        val bookId = 999L
        every { bookUseCase.reserveBook(bookId) } throws BookNotFoundException(bookId)

        mockMvc.post("/books/$bookId/reserve") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }
})