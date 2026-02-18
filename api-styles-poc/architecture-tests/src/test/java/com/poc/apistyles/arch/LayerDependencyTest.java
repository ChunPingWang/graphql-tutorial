package com.poc.apistyles.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

class LayerDependencyTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
            .importPackages(
                "com.poc.apistyles.domain..",
                "com.poc.apistyles.application..",
                "com.poc.apistyles.adapter..",
                "com.poc.apistyles.infrastructure.."
            );
    }

    @Test
    void domainShouldNotHaveCyclicDependencies() {
        slices()
            .matching("com.poc.apistyles.domain.(*)..")
            .should()
            .beFreeOfCycles()
            .check(importedClasses);
    }

    @Test
    void applicationShouldNotHaveCyclicDependencies() {
        slices()
            .matching("com.poc.apistyles.application.(*)..")
            .should()
            .beFreeOfCycles()
            .check(importedClasses);
    }

    @Test
    void adapterShouldOnlyDependOnApplicationOrDomain() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.adapter..")
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(
                "com.poc.apistyles.domain..",
                "com.poc.apistyles.application..",
                "java..",
                "org.springframework..",
                "jakarta..",
                "io.grpc.."
            );

        rule.check(importedClasses);
    }

    @Test
    void infrastructureShouldOnlyDependOnDomainOrApplication() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.infrastructure..")
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(
                "com.poc.apistyles.domain..",
                "com.poc.apistyles.application..",
                "java..",
                "org.springframework..",
                "jakarta..",
                "org.hibernate.."
            );

        rule.check(importedClasses);
    }

    @Test
    void domainModelShouldNotDependOnOtherLayers() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.model..")
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(
                "com.poc.apistyles.domain.model..",
                "java.."
            );

        rule.check(importedClasses);
    }
}
