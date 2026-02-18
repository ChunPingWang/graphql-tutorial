package com.poc.apistyles.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        ArchRule rule = ArchRuleDefinition
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
        ArchRule rule = ArchRuleDefinition
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
        ArchRule rule = ArchRuleDefinition
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
        ArchRule rule = ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.port..")
            .and()
            .areTopLevelClasses()
            .should()
            .beInterfaces();

        rule.check(importedClasses);
    }

    @Test
    void inboundPortsShouldBeInterfaces() {
        ArchRule rule = ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.port.inbound..")
            .and()
            .areTopLevelClasses()
            .should()
            .beInterfaces();

        rule.check(importedClasses);
    }

    @Test
    void outboundPortsShouldBeInterfaces() {
        ArchRule rule = ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.port.outbound..")
            .and()
            .areTopLevelClasses()
            .should()
            .beInterfaces();

        rule.check(importedClasses);
    }

}
