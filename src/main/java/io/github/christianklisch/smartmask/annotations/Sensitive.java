package io.github.christianklisch.smartmask.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import io.github.christianklisch.smartmask.MaskType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import java.lang.annotation.*;

/**
 * Annotation to mark a field as sensitive, triggering masking during serialization.
 * <p>
 * This annotation can be applied to fields or getter methods in your model classes
 * to indicate that the data should be masked when serialized to JSON or logged.
 * The masking behavior can be customized through various parameters.
 * </p>
 * <p>
 * Example usage:
 * </p>
 * <pre>
 * public class User {
 *     private String username;
 *     
 *     &#64;Sensitive(type = MaskType.EMAIL)
 *     private String email;
 *     
 *     &#64;Sensitive(showFirst = 0, showLast = 4, maskChar = '*')
 *     private String creditCardNumber;
 *     
 *     &#64;Sensitive(rolesAllowed = {"ROLE_ADMIN", "ROLE_SUPPORT"})
 *     private String socialSecurityNumber;
 *     
 *     // getters and setters
 * }
 * </pre>
 * <p>
 * When this object is serialized to JSON or logged, the sensitive fields will be masked
 * according to their configuration, unless the current user has one of the specified roles.
 * </p>
 * 
 * @see io.github.christianklisch.smartmask.MaskType
 * @see io.github.christianklisch.smartmask.jackson.SensitiveDataSerializer
 * @see io.github.christianklisch.smartmask.security.SecurityContextProvider
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD}) // Can be applied to fields or getters
@JacksonAnnotationsInside // Important for Jackson to recognize this as a "meta-annotation"
public @interface Sensitive {

    /**
     * Defines the type of masking to apply (e.g., EMAIL, CREDIT_CARD, GENERIC).
     * <p>
     * Each masking type implements a specific algorithm optimized for that data type.
     * For example, EMAIL masking preserves the domain part while masking the local part,
     * and CREDIT_CARD masking typically shows only the last 4 digits.
     * </p>
     * <p>
     * Defaults to GENERIC, which can be customized with showFirst, showLast, and maskChar.
     * </p>
     * 
     * @return The masking type to apply
     * @see io.github.christianklisch.smartmask.MaskType
     */
    @NonNull
    MaskType type() default MaskType.GENERIC;

    /**
     * For GENERIC masking, specifies how many characters to show from the start.
     * <p>
     * Example: showFirst = 3 for "password" → "pas****"
     * </p>
     * <p>
     * This parameter is primarily used with the GENERIC masking type. For specialized
     * masking types like EMAIL or CREDIT_CARD, this parameter is ignored in favor of
     * type-specific masking algorithms.
     * </p>
     * <p>
     * If both showFirst and showLast are specified, both will be applied.
     * </p>
     * 
     * @return The number of characters to show from the start
     */
    int showFirst() default 0;

    /**
     * For GENERIC masking, specifies how many characters to show from the end.
     * <p>
     * Example: showLast = 4 for "1234567890" → "******7890"
     * </p>
     * <p>
     * This parameter is primarily used with the GENERIC masking type. For specialized
     * masking types like EMAIL or CREDIT_CARD, this parameter is ignored in favor of
     * type-specific masking algorithms.
     * </p>
     * <p>
     * If both showFirst and showLast are specified, both will be applied.
     * </p>
     * 
     * @return The number of characters to show from the end
     */
    int showLast() default 0;

    /**
     * Specifies the roles that are allowed to see the unmasked value.
     * <p>
     * If the current user has any of these roles, the field will not be masked.
     * Uses Spring Security role names (e.g., "ROLE_ADMIN", "SUPPORT_STAFF").
     * </p>
     * <p>
     * If empty, the field is always masked unless a custom SecurityContextProvider allows it.
     * </p>
     * <p>
     * Example: rolesAllowed = {"ROLE_ADMIN", "ROLE_SUPPORT"} will show the unmasked value
     * to users with either the ROLE_ADMIN or ROLE_SUPPORT role.
     * </p>
     * 
     * @return Array of role names that are allowed to see the unmasked value
     * @see io.github.christianklisch.smartmask.security.SecurityContextProvider
     */
    @NonNull
    String[] rolesAllowed() default {};

    /**
     * The character used for masking.
     * <p>
     * This character replaces the sensitive parts of the data.
     * </p>
     * <p>
     * Example: maskChar = '#' for "1234567890" with showLast = 4 → "######7890"
     * </p>
     * 
     * @return The character to use for masking
     */
    char maskChar() default '*';
}
