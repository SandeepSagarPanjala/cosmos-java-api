package com.cosmos.api.sample;

import com.cosmos.api.annotation.custom.Classified;
import com.cosmos.api.annotation.custom.DataClassification;
import com.cosmos.api.annotation.custom.GeneratorHint;
import com.cosmos.api.annotation.custom.IdempotentEndpoint;
import com.cosmos.api.annotation.custom.TeamOwned;

/**
 * Dummy class for learning: it only exists so you can point reflection (see test) at real annotated elements.
 * Not registered as a Spring bean by default.
 */
@TeamOwned(team = "API Platform", contact = "api-platform@example.com")
@Classified(DataClassification.INTERNAL)
@GeneratorHint(note = "This hint exists only in source; runtime reflection cannot see it.")
public final class CustomAnnotationShowcase {

    private CustomAnnotationShowcase() {
    }

    @IdempotentEndpoint
    @TeamOwned(team = "Payments")
    @Classified(DataClassification.RESTRICTED)
    public static void recordTransfer() {
        // no-op — annotations are the lesson
    }

    @GeneratorHint(note = "Another SOURCE-only tag")
    public static void plainOperation() {
    }
}
