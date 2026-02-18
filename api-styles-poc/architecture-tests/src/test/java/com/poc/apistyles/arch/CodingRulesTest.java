package com.poc.apistyles.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

class CodingRulesTest {

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
    void noFieldInjectionWithAutowired() {
        ArchRule rule = noFields()
            .should()
            .beAnnotatedWith(org.springframework.beans.factory.annotation.Autowired.class)
            .because("Field injection is not recommended. Use constructor injection instead.");

        rule.check(importedClasses);
    }

    @Test
    void noFieldInjectionWithInject() {
        ArchRule rule = noFields()
            .should()
            .beAnnotatedWith(Inject.class)
            .because("Field injection is not recommended. Use constructor injection instead.");

        rule.check(importedClasses);
    }

    @Test
    void servicesShouldUseConstructorInjection() {
        ArchRule rule = classes()
            .that()
            .haveSimpleNameEndingWith("Service")
            .and()
            .areNotInterfaces()
            .should()
            .haveAtLeastOneConstructor()
            .andShould()
            .haveAtLeastOneConstructorAnnotatedWith(org.springframework.beans.factory.annotation.Autowired.class)
            .orShould()
            .haveAtLeastOneConstructorAnnotatedWith(jakarta.inject.Inject.class)
            .orShould()
            .haveAtLeastOneConstructorWithParameters();

        rule.check(importedClasses);
    }

    @Test
    void noPublicFields() {
        ArchRule rule = noFields()
            .should()
            .bePublic()
            .because("Classes should encapsulate their fields. Use getters/setters instead.");

        rule.check(importedClasses);
    }

    @Test
    void domainClassesShouldNotUseLombokData() {
        ArchRule rule = noClasses()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.model..")
            .should()
            .beAnnotatedWith(org.projectlombok.Data.class)
            .because("Domain model classes should not use @Data as it generates hashCode/equals based on all fields");

        rule.check(importedClasses);
    }

    @Test
    void domainClassesShouldNotUseLombokSetter() {
        ArchRule rule = noClasses()
            .that()
            .resideInAPackage("com.poc.apistyles.domain.model..")
            .should()
            .beAnnotatedWith(org.projectlombok.Setter.class)
            .because("Domain model classes should use value objects or explicit setters for encapsulation");

        rule.check(importedClasses);
    }

    @Test
    void noClassesShouldThrowGenericException() {
        ArchRule rule = noMethods()
            .should()
            .throwExceptionTypesOfType(Exception.class)
            .because("Methods should throw specific exceptions, not generic Exception");

        rule.check(importedClasses);
    }

    @Test
    void noClassesShouldThrowRuntimeException() {
        ArchRule rule = noMethods()
            .should()
            .throwExceptionTypesOfType(RuntimeException.class)
            .because("Methods should throw specific exceptions, not generic RuntimeException");

        rule.check(importedClasses);
    }
}
