package com.cosmos.api.annotation.custom;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>Single logical member</b> named {@code value} — allows shorthand {@code @Classified(DataClassification.INTERNAL)}
 * instead of {@code @Classified(value = DataClassification.INTERNAL)}.
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Classified {

    DataClassification value();
}
