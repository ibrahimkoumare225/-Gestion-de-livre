package fr.koumare.gestion_de_livre.infrastructure.driving.web

import fr.koumare.gestion_de_livre.domain.model.Book
import com.ninjasquad.springmockk.MockkBean
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

    "POST /books should create book and return 201" {
        val bookDTO = """{"title":"Test Book","author":"Test Author"}"""
        val expectedBook = Book("Test Book", "Test Author")
        every { bookUseCase.addBook(expectedBook) } returns Unit

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = bookDTO
        }.andExpect {
            status { isCreated() }
        }
    }

    "GET /books should return all books sorted by title" {
        val books = listOf(
            Book("Book B", "Author B", false),
            Book("Book A", "Author A", false),
            Book("Book C", "Author C", false)
        )
        every { bookUseCase.listBooks() } returns books.sortedBy { it.title }

        mockMvc.get("/books") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                json(
                    """
                    [
                        {"title":"Book A","author":"Author A","isAvailable":true},
                        {"title":"Book B","author":"Author B","isAvailable":true},
                        {"title":"Book C","author":"Author C","isAvailable":true}
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

    "POST /books/reserve should reserve book and return 200" {
        val reservationDTO = """{"title":"Test Book","author":"Test Author"}"""
        val reservedBook = Book("Test Book", "Test Author", isReserved = true)
        every { bookUseCase.reserveBook("Test Book", "Test Author") } returns reservedBook

        mockMvc.post("/books/reserve") {
            contentType = MediaType.APPLICATION_JSON
            content = reservationDTO
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                json("""{"title":"Test Book","author":"Test Author","isAvailable":false}""")
            }
        }
    }

    "POST /books/reserve should return 409 when book cannot be reserved" {
        val reservationDTO = """{"title":"Test Book","author":"Test Author"}"""
        every { bookUseCase.reserveBook("Test Book", "Test Author") } returns null

        mockMvc.post("/books/reserve") {
            contentType = MediaType.APPLICATION_JSON
            content = reservationDTO
        }.andExpect {
            status { isConflict() }
        }
    }
})