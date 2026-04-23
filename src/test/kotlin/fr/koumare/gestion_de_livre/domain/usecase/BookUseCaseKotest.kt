package fr.koumare.gestion_de_livre.domain.usecase

import fr.koumare.gestion_de_livre.domain.model.Book
import fr.koumare.gestion_de_livre.domain.port.BookPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class BookUseCaseKotest : FunSpec({

    val bookPort = mockk<BookPort>()
    val bookUseCase = BookUseCase(bookPort)

    beforeTest {
        clearMocks(bookPort)
    }

    test("should return all books sorted by title") {
        every { bookPort.findAll() } returns listOf(
            Book(title = "Les Misérables", author = "Victor Hugo"),
            Book(title = "Hamlet", author = "William Shakespeare")
        )

        val result = bookUseCase.listBooks()

        result.size shouldBe 2
        result[0].title shouldBe "Hamlet"
        result[1].title shouldBe "Les Misérables"
    }

    test("should add book and return it with generated id") {
        val book = Book(title = "Les Misérables", author = "Victor Hugo")
        val savedBook = book.copy(id = 1)
        every { bookPort.save(book) } returns savedBook

        val result = bookUseCase.addBook(book)

        result.id shouldBe 1
        result.title shouldBe "Les Misérables"
        result.author shouldBe "Victor Hugo"
        verify(exactly = 1) { bookPort.save(book) }
    }

    test("should reserve book successfully") {
        val book = Book(id = 1, title = "Les Misérables", author = "Victor Hugo")
        val reservedBook = book.reserve()
        every { bookPort.findById(1) } returns book
        every { bookPort.reserveBook(reservedBook) } returns reservedBook

        val result = bookUseCase.reserveBook(1)

        result.isReserved shouldBe true
        result.id shouldBe 1
        verify(exactly = 1) { bookPort.findById(1) }
        verify(exactly = 1) { bookPort.reserveBook(reservedBook) }
    }

    test("should throw BookNotFoundException when reserving unknown book") {
        every { bookPort.findById(1) } returns null

        shouldThrow<BookNotFoundException> {
            bookUseCase.reserveBook(1)
        }

        verify(exactly = 1) { bookPort.findById(1) }
    }

    test("should throw BookAlreadyReservedException when reserving already reserved book") {
        val reservedBook = Book(id = 1, title = "Les Misérables", author = "Victor Hugo", isReserved = true)
        every { bookPort.findById(1) } returns reservedBook

        shouldThrow<BookAlreadyReservedException> {
            bookUseCase.reserveBook(1)
        }

        verify(exactly = 1) { bookPort.findById(1) }
    }

    test("should throw IllegalArgumentException when adding book with blank title") {
        shouldThrow<IllegalArgumentException> {
            bookUseCase.addBook(Book(title = "", author = "Victor Hugo"))
        }
    }

    test("should throw IllegalArgumentException when adding book with blank author") {
        shouldThrow<IllegalArgumentException> {
            bookUseCase.addBook(Book(title = "Les Misérables", author = ""))
        }
    }
})