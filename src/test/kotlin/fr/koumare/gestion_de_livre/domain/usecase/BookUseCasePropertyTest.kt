package fr.koumare.gestion_de_livre.domain.usecase

import fr.koumare.gestion_de_livre.domain.model.Book
import net.jqwik.api.*
import net.jqwik.api.constraints.IntRange
import net.jqwik.api.constraints.Size
import net.jqwik.kotlin.api.combine
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BookUseCasePropertyTest {

    @Property
    fun `adding valid books should increase repository size`(
        @ForAll("validTitleLists") initialTitles: List<String>,
        @ForAll("validAuthors") author: String,
        @ForAll("validTitles") title: String
    ) {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        initialTitles.map { Book(it, author) }.forEach { useCase.addBook(it) }
        val initialSize = repo.findAll().size

        val book = Book(title, author)
        useCase.addBook(book)

        assertEquals(initialSize + 1, repo.findAll().size)
        assertTrue(repo.findAll().contains(book))
    }

    @Property
    fun `adding invalid book with blank title should throw exception`(
        @ForAll("blankStrings") blankTitle: String,
        @ForAll("validAuthors") author: String
    ) {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        assertFailsWith<IllegalArgumentException> {
            useCase.addBook(Book(blankTitle, author))
        }
    }

    @Property
    fun `adding invalid book with blank author should throw exception`(
        @ForAll("validTitles") title: String,
        @ForAll("blankStrings") blankAuthor: String
    ) {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        assertFailsWith<IllegalArgumentException> {
            useCase.addBook(Book(title, blankAuthor))
        }
    }

    @Property
    fun `listBooks should return books sorted by title`(
        @ForAll("validTitleListsNonEmpty") titles: List<String>,
        @ForAll("validAuthors") author: String
    ) {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        val books = titles.map { Book(it, author) }
        books.shuffled().forEach { useCase.addBook(it) }

        val result = useCase.listBooks()

        assertEquals(books.size, result.size)
        assertEquals(result.sortedBy { it.title }, result)
        assertTrue(result.containsAll(books))
    }

    @Property
    fun `listBooks should be idempotent`(
        @ForAll("validTitleLists") titles: List<String>,
        @ForAll("validAuthors") author: String
    ) {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        val books = titles.map { Book(it, author) }
        books.forEach { useCase.addBook(it) }

        val result1 = useCase.listBooks()
        val result2 = useCase.listBooks()

        assertEquals(result1, result2)
    }

    @Property
    fun `adding same book multiple times should work`(
        @ForAll("validTitles") title: String,
        @ForAll("validAuthors") author: String,
        @ForAll @IntRange(min = 1, max = 5) times: Int
    ) {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        val book = Book(title, author)
        repeat(times) {
            useCase.addBook(book)
        }

        val result = repo.findAll()
        assertEquals(times, result.size)
        assertTrue(result.all { it == book })
    }

    @Property
    fun `reserving a book should mark it as reserved`(
        @ForAll("validTitles") title: String,
        @ForAll("validAuthors") author: String
    ) {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        val book = Book(title, author)
        useCase.addBook(book)

        val reservedBook = useCase.reserveBook(title, author)

        assertEquals(title, reservedBook?.title)
        assertEquals(author, reservedBook?.author)
        assertTrue(reservedBook?.isReserved == true)
        assertTrue(reservedBook?.isAvailable == false)
    }

    @Property
    fun `reserving already reserved book should fail`(
        @ForAll("validTitles") title: String,
        @ForAll("validAuthors") author: String
    ) {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        val book = Book(title, author)
        useCase.addBook(book)
        useCase.reserveBook(title, author)

        assertFailsWith<IllegalArgumentException> {
            useCase.reserveBook(title, author)
        }
    }

    @Property
    fun `reserving non-existent book should fail`(
        @ForAll("validTitles") title: String,
        @ForAll("validAuthors") author: String,
        @ForAll("validTitles") wrongTitle: String
    ) {
        val repo = FakeBookPort()
        val useCase = BookUseCase(repo)

        val book = Book(title, author)
        useCase.addBook(book)

        // Ensure wrongTitle is different from title
        val actualWrongTitle = if (wrongTitle == title) "Different $wrongTitle" else wrongTitle

        assertFailsWith<IllegalArgumentException> {
            useCase.reserveBook(actualWrongTitle, author)
        }
    }
    @Provide
    fun validTitles(): Arbitrary<String> = Arbitraries.strings()
        .withCharRange('a', 'z')
        .withCharRange('A', 'Z')
        .withCharRange('0', '9')
        .withChars(' ', '-', '_', '.', ',')
        .ofMinLength(1)
        .ofMaxLength(100)
        .filter { it.isNotBlank() }

    @Provide
    fun validTitleLists(): Arbitrary<List<String>> = validTitles().list().ofMinSize(0).ofMaxSize(10)

    @Provide
    fun validTitleListsNonEmpty(): Arbitrary<List<String>> = validTitles().list().ofMinSize(1).ofMaxSize(20)

    @Provide
    fun validAuthors(): Arbitrary<String> = Arbitraries.strings()
        .withCharRange('a', 'z')
        .withCharRange('A', 'Z')
        .withChars(' ', '-', '_', '.', ',')
        .ofMinLength(1)
        .ofMaxLength(50)
        .filter { it.isNotBlank() }

    @Provide
    fun blankStrings(): Arbitrary<String> = Arbitraries.oneOf(
        Arbitraries.just(""),
        Arbitraries.just("   "),
        Arbitraries.just("\t"),
        Arbitraries.just("\n"),
        Arbitraries.strings().withChars(' ', '\t', '\n').ofMinLength(1).ofMaxLength(10)
    )
}