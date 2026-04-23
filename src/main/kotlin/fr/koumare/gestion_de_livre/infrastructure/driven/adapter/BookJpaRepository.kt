package fr.koumare.gestion_de_livre.infrastructure.driven.adapter

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BookJpaRepository : JpaRepository<BookEntity, Long> {

    fun findByTitleAndAuthor(title: String, author: String): BookEntity?

    @Modifying
    @Query("UPDATE BookEntity b SET b.isReserved = true WHERE b.title = :title AND b.author = :author AND b.isReserved = false")
    fun reserveBook(@Param("title") title: String, @Param("author") author: String): Int
}