package com.poc.apistyles.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.library.plantuml.PlantUmlArchCondition;
import com.tngtech.archunit.library.plantuml.PlantUmlArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.plantuml.PlantUmlArchCondition.Configuration.ConfigurationBuilder;

class HexagonalArchitectureTest {

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
    void domainLayerShouldNotDependOnAdapters() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .noClasses()
            .that()
            .resideInAPackage("com.poc.apistyles.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("com.poc.apistyles.adapter..");

        rule.check(importedClasses);
    }

    @Test
    void domainLayerShouldNotDependOnInfrastructure() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .noClasses()
            .that()
            .resideInAPackage("com.poc.apistyles.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("com.poc.apistyles.infrastructure..");

        rule.check(importedClasses);
    }

    @Test
    void domainLayerShouldNotDependOnApplication() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .noClasses()
            .that()
            .resideInAPackage("com.poc.apistyles.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("com.poc.apistyles.application..");

        rule.check(importedClasses);
    }

    @Test
    void portsShouldBeInterfaces() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.port..")
            .should()
            .beInterfaces();

        rule.check(importedClasses);
    }

    @Test
    void inboundPortsShouldBeInterfaces() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.port.inbound..")
            .should()
            .beInterfaces();

        rule.check(importedClasses);
    }

    @Test
    void outboundPortsShouldBeInterfaces() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.port.outbound..")
            .should()
            .beInterfaces();

        rule.check(importedClasses);
    }

    @Test
    void adaptersShouldOnlyUseOutboundPorts() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.adapter..")
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(
                "com.poc.apistyles.domain.port.outbound..",
                "com.poc.apistyles.domain.model..",
                "java..",
                "org.springframework..",
                "jakarta.."
            );

        rule.check(importedClasses);
    }
}
