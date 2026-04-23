package fr.koumare.gestion_de_livre

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures.onionArchitecture
import org.junit.jupiter.api.Tag

@AnalyzeClasses(packages = ["fr.koumare.gestion_de_livre"])
@Tag("archunit")
class ArchitectureTest {

    @ArchTest
    val onionArchitectureRule: ArchRule = onionArchitecture()
        .domainModels("..domain.model..")
        .domainServices("..domain.usecase..")
        .applicationServices("..infrastructure.application..")
        .adapter("driven", "..infrastructure.driven..")
        .adapter("driving", "..infrastructure.driving..")
}