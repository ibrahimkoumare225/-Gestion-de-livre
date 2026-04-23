package fr.koumare.gestion_de_livre

import fr.koumare.gestion_de_livre.infrastructure.driving.web.dto.BookDTO
import fr.koumare.gestion_de_livre.infrastructure.driving.web.dto.BookReservationDTO
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.spring.CucumberContextConfiguration
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@ActiveProfiles("testComponent")
class BookStepDefs(
    @LocalServerPort val port: Int
) {

    init {
        RestAssured.port = port
    }

    @Given("the book management system is running")
    fun theBookManagementSystemIsRunning() {
        // System is already running via Spring Boot test
    }

    @When("I add a book with title {string} and author {string}")
    fun iAddABookWithTitleAndAuthor(title: String, author: String) {
        val bookDTO = BookDTO(title, author)
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(bookDTO)
            .post("/books")
            .then()
            .statusCode(201)
    }

    @Then("the book list should contain {int} book(s)")
    fun theBookListShouldContainBooks(count: Int) {
        RestAssured.given()
            .get("/books")
            .then()
            .statusCode(200)
            .body("size()", equalTo(count))
    }

    @Then("the book list should contain a book with title {string} and author {string}")
    fun theBookListShouldContainABookWithTitleAndAuthor(title: String, author: String) {
        RestAssured.given()
            .get("/books")
            .then()
            .statusCode(200)
            .body("find { it.title == '$title' && it.author == '$author' }", equalTo(mapOf("title" to title, "author" to author, "isAvailable" to true)))
    }

    @When("I reserve the book with title {string} and author {string}")
    fun iReserveTheBookWithTitleAndAuthor(title: String, author: String) {
        val reservationDTO = BookReservationDTO(title, author)
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(reservationDTO)
            .post("/books/reserve")
            .then()
            .statusCode(200)
    }

    @Then("the book should be reserved")
    fun theBookShouldBeReserved() {
        // This is verified by the successful reservation call
    }

    @Then("the book list should show the book as not available")
    fun theBookListShouldShowTheBookAsNotAvailable() {
        RestAssured.given()
            .get("/books")
            .then()
            .statusCode(200)
            .body("find { it.isAvailable == false }", equalTo(mapOf("title" to "Clean Code", "author" to "Robert C. Martin", "isAvailable" to false)))
    }

    @When("I try to reserve the book with title {string} and author {string}")
    fun iTryToReserveTheBookWithTitleAndAuthor(title: String, author: String) {
        val reservationDTO = BookReservationDTO(title, author)
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(reservationDTO)
            .post("/books/reserve")
            .then()
            .statusCode(409) // Conflict status for failed reservation
    }

    @Then("the reservation should fail")
    fun theReservationShouldFail() {
        // This is verified by the status code check in the When step
    }
}