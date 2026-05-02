package com.cosmos.api.annotation.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>SOURCE retention</b> — stripped from bytecode after compile. <b>Not visible</b> to reflection at runtime.
 * Used for tooling, documentation generators, or IDEs; not for Spring beans unless you add a compile-time processor.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface GeneratorHint {

    String note();
}
