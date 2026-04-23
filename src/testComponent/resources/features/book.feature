Feature: Book Management

  Scenario: Add a book to the system
    Given the book management system is running
    When I add a book with title "Clean Code" and author "Robert C. Martin"
    Then the book list should contain 1 book
    And the book list should contain a book with title "Clean Code" and author "Robert C. Martin"

  Scenario: Add multiple books to the system
    Given the book management system is running
    When I add a book with title "Clean Code" and author "Robert C. Martin"
    And I add a book with title "Domain-Driven Design" and author "Eric Evans"
    Then the book list should contain 2 books
    And the book list should contain a book with title "Clean Code" and author "Robert C. Martin"
    And the book list should contain a book with title "Domain-Driven Design" and author "Eric Evans"

  Scenario: Reserve an available book
    Given the book management system is running
    And I add a book with title "Clean Code" and author "Robert C. Martin"
    When I reserve the book with title "Clean Code" and author "Robert C. Martin"
    Then the book should be reserved
    And the book list should show the book as not available

  Scenario: Try to reserve an already reserved book
    Given the book management system is running
    And I add a book with title "Clean Code" and author "Robert C. Martin"
    And I reserve the book with title "Clean Code" and author "Robert C. Martin"
    When I try to reserve the book with title "Clean Code" and author "Robert C. Martin"
    Then the reservation should fail

  Scenario: Try to reserve a non-existent book
    Given the book management system is running
    When I try to reserve the book with title "Non-existent Book" and author "Unknown Author"
    Then the reservation should fail