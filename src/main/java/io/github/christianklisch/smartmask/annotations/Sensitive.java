package io.github.christianklisch.smartmask.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import io.github.christianklisch.smartmask.MaskType;
import java.lang.annotation.*;

/**
 * Annotation to mark a field as sensitive, triggering masking during serialization.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD}) // Can be applied to fields or getters
@JacksonAnnotationsInside // Important for Jackson to recognize this as a "meta-annotation"
public @interface Sensitive {

    /**
     * Defines the type of masking to apply (e.g., EMAIL, CREDIT_CARD, GENERIC).
     * Defaults to GENERIC.
     */
    MaskType type() default MaskType.GENERIC;

    /**
     * For GENERIC masking, specifies how many characters to show from the start.
     * e.g., showFirst = 3 for "password" -> "pas****"
     * Only applies if showLast is 0.
     */
    int showFirst() default 0;

    /**
     * For GENERIC masking, specifies how many characters to show from the end.
     * e.g., showLast = 4 for "1234567890" -> "******7890"
     */
    int showLast() default 0;

    /**
     * Specifies the roles that are allowed to see the unmasked value.
     * If the current user has any of these roles, the field will not be masked.
     * Uses Spring Security role names (e.g., "ROLE_ADMIN", "SUPPORT_STAFF").
     * If empty, the field is always masked unless a custom SecurityContextProvider allows it.
     */
    String[] rolesAllowed() default {};

    /**
     * The character used for masking. Defaults to '*'.
     */
    char maskChar() default '*';
}