package com.poc.apistyles.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
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
        ArchRule rule = ArchRuleDefinition
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
        ArchRule rule = ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.application..")
            .and()
            .areTopLevelClasses()
            .and()
            .haveSimpleNameNotContaining("Configuration")
            .should()
            .haveSimpleNameContaining("Service");

        rule.check(importedClasses);
    }

    @Test
    void portInterfacesShouldEndWithPort() {
        ArchRule rule = ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.port..")
            .and()
            .areInterfaces()
            .should()
            .haveSimpleNameEndingWith("Port")
            .orShould()
            .haveSimpleNameEndingWith("Repository")
            .orShould()
            .haveSimpleNameEndingWith("Service");

        rule.check(importedClasses);
    }

    @Test
    void adapterClassesShouldBeNamedWithAdapterTerm() {
        ArchRule rule = ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.adapter..")
            .and()
            .resideOutsideOfPackage("com.poc.apistyles.adapter.grpc.protobuf..")
            .and()
            .areTopLevelClasses()
            .and()
            .areNotInterfaces()
            .should()
            .haveSimpleNameEndingWith("Adapter")
            .orShould()
            .haveSimpleNameEndingWith("Controller")
            .orShould()
            .haveSimpleNameEndingWith("Resolver")
            .orShould()
            .haveSimpleNameEndingWith("Config")
            .orShould()
            .haveSimpleNameEndingWith("Handler")
            .orShould()
            .haveSimpleNameEndingWith("Service")
            .orShould()
            .haveSimpleNameEndingWith("Endpoint")
            .orShould()
            .haveSimpleNameEndingWith("Application")
            .orShould()
            .haveSimpleNameEndingWith("Request")
            .orShould()
            .haveSimpleNameEndingWith("Response")
            .orShould()
            .haveSimpleNameEndingWith("Input")
            .orShould()
            .haveSimpleNameEndingWith("Data");

        rule.check(importedClasses);
    }

    @Test
    void repositoryInterfacesShouldEndWithRepository() {
        ArchRule rule = ArchRuleDefinition
            .classes()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.port.outbound..")
            .and()
            .areInterfaces()
            .should()
            .haveSimpleNameEndingWith("Repository");

        rule.check(importedClasses);
    }

    @Test
    void inboundServiceInterfacesShouldBeInInboundPackage() {
        ArchRule rule = ArchRuleDefinition
            .classes()
            .that()
            .haveSimpleNameEndingWith("Service")
            .and()
            .resideInAPackage("com.poc.apistyles.domain.port.inbound..")
            .and()
            .areInterfaces()
            .should()
            .beInterfaces();

        rule.check(importedClasses);
    }
}
