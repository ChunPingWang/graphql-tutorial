package com.poc.apistyles.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
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
            .allowEmptyShould(true)
            .check(importedClasses);
    }

    @Test
    void adapterShouldOnlyDependOnApplicationOrDomain() {
        ArchRule rule = ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.adapter..")
            .and()
            .resideOutsideOfPackage("com.poc.apistyles.adapter.grpc.protobuf..")
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(
                "com.poc.apistyles.domain..",
                "com.poc.apistyles.application..",
                "com.poc.apistyles.adapter..",
                "java..",
                "org.springframework..",
                "jakarta..",
                "io.grpc..",
                "net.devh..",
                "graphql..",
                "io.swagger..",
                "com.google.protobuf..",
                "javax.annotation.."
            );

        rule.check(importedClasses);
    }

    @Test
    void infrastructureShouldOnlyDependOnDomainOrApplication() {
        ArchRule rule = ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.infrastructure..")
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(
                "com.poc.apistyles.domain..",
                "com.poc.apistyles.application..",
                "com.poc.apistyles.infrastructure..",
                "java..",
                "org.springframework..",
                "jakarta..",
                "org.hibernate..",
                "org.flywaydb..",
                "org.postgresql.."
            );

        rule.check(importedClasses);
    }

    @Test
    void domainModelShouldNotDependOnOtherLayers() {
        ArchRule rule = ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.model..")
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(
                "com.poc.apistyles.domain.model..",
                "com.poc.apistyles.domain.exception..",
                "java.."
            );

        rule.check(importedClasses);
    }
}
