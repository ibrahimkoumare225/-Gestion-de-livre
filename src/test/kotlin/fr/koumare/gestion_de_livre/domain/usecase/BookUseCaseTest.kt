package fr.koumare.gestion_de_livre.domain.usecase

import fr.koumare.gestion_de_livre.domain.model.Book
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BookUseCaseTest {

    @Test
    fun `should add book when valid`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        val book = Book(1L, "Test Book", "Test Author", false)
        useCase.addBook(book)

        assertEquals(1, repo.findAll().size)
        assertEquals(book, repo.findAll().first())
    }

    @Test
    fun `should throw exception when adding invalid book with blank title`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        assertFailsWith<IllegalArgumentException> {
            useCase.addBook(Book(1L, "", "Author", false))
        }
    }

    @Test
    fun `should throw exception when adding invalid book with blank author`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        assertFailsWith<IllegalArgumentException> {
            useCase.addBook(Book(1L, "Title", "", false))
        }
    }

    @Test
    fun `should list books sorted by title`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        val book1 = Book(1L, "B Book", "Author B", false)
        val book2 = Book(2L, "A Book", "Author A", false)
        useCase.addBook(book1)
        useCase.addBook(book2)

        val result = useCase.listBooks()

        assertEquals(2, result.size)
        assertEquals("A Book", result[0].title)
        assertEquals("B Book", result[1].title)
    }

    @Test
    fun `should return empty list when no books`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        val result = useCase.listBooks()

        assertEquals(0, result.size)
    }

    @Test
    fun `should reserve available book`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        val book = Book(1L, "Test Book", "Test Author", false)
        useCase.addBook(book)

        val reservedBook = useCase.reserveBook(1L)

        assertEquals("Test Book", reservedBook.title)
        assertEquals("Test Author", reservedBook.author)
        assertEquals(true, reservedBook.isReserved)
        assertEquals(false, reservedBook.isAvailable)
    }

    @Test
    fun `should throw exception when reserving non-existent book`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        assertFailsWith<BookNotFoundException> {
            useCase.reserveBook(999L)
        }
    }

    @Test
    fun `should throw exception when reserving already reserved book`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        val book = Book(1L, "Test Book", "Test Author", false)
        useCase.addBook(book)
        useCase.reserveBook(1L)

        assertFailsWith<BookAlreadyReservedException> {
            useCase.reserveBook(1L)
        }
    }
}
