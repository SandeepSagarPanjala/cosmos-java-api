package com.cosmos.api.annotation.custom;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>Marker annotation</b> — no attributes. Marks a design intent (e.g. “this POST must be safe to retry”).
 * <p>
 * Nothing in the JVM runs this by itself; <i>you</i> (or a framework like Spring AOP) read it via reflection
 * or an annotation processor and apply behavior.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IdempotentEndpoint {
}
