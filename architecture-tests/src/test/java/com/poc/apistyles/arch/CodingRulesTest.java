package com.poc.apistyles.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

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
    void noPublicFields() {
        ArchRule rule = fields()
            .that()
            .areDeclaredInClassesThat()
            .resideOutsideOfPackage("com.poc.apistyles.adapter.grpc.protobuf..")
            .and()
            .areDeclaredInClassesThat()
            .areNotEnums()
            .should()
            .notBePublic()
            .because("Classes should encapsulate their fields. Use getters/setters instead.");

        rule.check(importedClasses);
    }
}
