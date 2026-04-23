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

        val book = Book("Test Book", "Test Author")
        useCase.addBook(book)

        assertEquals(1, repo.findAll().size)
        assertEquals(book, repo.findAll().first())
    }

    @Test
    fun `should throw exception when adding invalid book with blank title`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        assertFailsWith<IllegalArgumentException> {
            useCase.addBook(Book("", "Author"))
        }
    }

    @Test
    fun `should throw exception when adding invalid book with blank author`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        assertFailsWith<IllegalArgumentException> {
            useCase.addBook(Book("Title", ""))
        }
    }

    @Test
    fun `should list books sorted by title`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        val book1 = Book("B Book", "Author B")
        val book2 = Book("A Book", "Author A")
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

        val book = Book("Test Book", "Test Author")
        useCase.addBook(book)

        val reservedBook = useCase.reserveBook("Test Book", "Test Author")

        assertEquals("Test Book", reservedBook?.title)
        assertEquals("Test Author", reservedBook?.author)
        assertEquals(true, reservedBook?.isReserved)
        assertEquals(false, reservedBook?.isAvailable)
    }

    @Test
    fun `should throw exception when reserving non-existent book`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        assertFailsWith<IllegalArgumentException> {
            useCase.reserveBook("Non-existent Book", "Unknown Author")
        }
    }

    @Test
    fun `should throw exception when reserving already reserved book`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        val book = Book("Test Book", "Test Author")
        useCase.addBook(book)
        useCase.reserveBook("Test Book", "Test Author")

        assertFailsWith<IllegalArgumentException> {
            useCase.reserveBook("Test Book", "Test Author")
        }
    }

    @Test
    fun `should throw exception when reserving with blank title`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        assertFailsWith<IllegalArgumentException> {
            useCase.reserveBook("", "Author")
        }
    }

    @Test
    fun `should throw exception when reserving with blank author`() {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        assertFailsWith<IllegalArgumentException> {
            useCase.reserveBook("Title", "")
        }
    }
}