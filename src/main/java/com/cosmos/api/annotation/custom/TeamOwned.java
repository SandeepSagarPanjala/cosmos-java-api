package com.cosmos.api.annotation.custom;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>Annotation with multiple members</b> — both {@code team} and {@code contact} are declared on use sites.
 * {@code contact} has a default so callers may omit it.
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TeamOwned {

    String team();

    String contact() default "";
}
