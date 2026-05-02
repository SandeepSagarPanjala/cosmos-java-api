package com.cosmos.api.sample;

import com.cosmos.api.annotation.custom.Classified;
import com.cosmos.api.annotation.custom.DataClassification;
import com.cosmos.api.annotation.custom.IdempotentEndpoint;
import com.cosmos.api.annotation.custom.TeamOwned;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Demonstrates how the <b>JVM</b> exposes {@link RetentionPolicy#RUNTIME} annotations via reflection.
 * Open this test and run it — read the assertions as documentation.
 */
class CustomAnnotationShowcaseTest {

    @Test
    void classLevelRuntimeAnnotations() throws Exception {
        Class<?> clazz = CustomAnnotationShowcase.class;

        TeamOwned owned = clazz.getAnnotation(TeamOwned.class);
        assertNotNull(owned);
        assertEquals("API Platform", owned.team());
        assertEquals("api-platform@example.com", owned.contact());

        Classified classified = clazz.getAnnotation(Classified.class);
        assertNotNull(classified);
        assertEquals(DataClassification.INTERNAL, classified.value());

        // SOURCE annotations like @GeneratorHint are NOT on the class at runtime:
        assertTrue(
                java.util.Arrays.stream(clazz.getAnnotations())
                        .map(Annotation::annotationType)
                        .noneMatch(t -> t.getSimpleName().equals("GeneratorHint")));
    }

    @Test
    void methodLevelMarkerAndMembers() throws Exception {
        Method m = CustomAnnotationShowcase.class.getMethod("recordTransfer");

        assertNotNull(m.getAnnotation(IdempotentEndpoint.class));

        TeamOwned team = m.getAnnotation(TeamOwned.class);
        assertNotNull(team);
        assertEquals("Payments", team.team());
        assertEquals("", team.contact()); // default was used

        Classified c = m.getAnnotation(Classified.class);
        assertEquals(DataClassification.RESTRICTED, c.value());
    }

    @Test
    void listAllRuntimeAnnotationsOnTransferMethod() throws Exception {
        Method m = CustomAnnotationShowcase.class.getMethod("recordTransfer");
        String[] names = java.util.Arrays.stream(m.getAnnotations())
                .map(a -> a.annotationType().getSimpleName())
                .sorted()
                .toArray(String[]::new);
        assertArrayEquals(new String[] { "Classified", "IdempotentEndpoint", "TeamOwned" }, names);
    }
}
