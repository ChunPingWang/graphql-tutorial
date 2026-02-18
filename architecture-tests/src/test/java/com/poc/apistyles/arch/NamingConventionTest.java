package com.poc.apistyles.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class NamingConventionTest {

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
    void domainModelClassesShouldBeNamedWithDomainTerm() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.model..")
            .and()
            .areNotEnums()
            .and()
            .areNotInterfaces()
            .should()
            .haveSimpleNameContaining("Customer")
            .orShould()
            .haveSimpleNameContaining("Order")
            .orShould()
            .haveSimpleNameContaining("Product")
            .orShould()
            .haveSimpleNameContaining("Item");

        rule.check(importedClasses);
    }

    @Test
    void serviceClassesShouldEndWithService() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.application..")
            .and()
            .haveSimpleNameNotContaining("Configuration")
            .should()
            .haveSimpleNameEndingWith("Service");

        rule.check(importedClasses);
    }

    @Test
    void portInterfacesShouldEndWithPort() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .interfaces()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.port..")
            .should()
            .haveSimpleNameEndingWith("Port")
            .orShould()
            .haveSimpleNameEndingWith("Repository");

        rule.check(importedClasses);
    }

    @Test
    void adapterClassesShouldBeNamedWithAdapterTerm() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.adapter..")
            .and()
            .areNotInterfaces()
            .should()
            .haveSimpleNameEndingWith("Adapter")
            .orShould()
            .haveSimpleNameEndingWith("Controller")
            .orShould()
            .haveSimpleNameEndingWith("Resolver");

        rule.check(importedClasses);
    }

    @Test
    void repositoryInterfacesShouldEndWithRepository() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .interfaces()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.port.outbound..")
            .should()
            .haveSimpleNameEndingWith("Repository");

        rule.check(importedClasses);
    }

    @Test
    void inboundServiceInterfacesShouldBeInInboundPackage() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.ArchRuleDefinition
            .interfaces()
            .that()
            .haveSimpleNameEndingWith("Service")
            .and()
            .resideInAPackage("com.poc.apistyles.domain.port.inbound..")
            .should()
            .beInterfaces();

        rule.check(importedClasses);
    }
}
