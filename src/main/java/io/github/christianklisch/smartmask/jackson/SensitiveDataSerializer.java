package io.github.christianklisch.smartmask.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.christianklisch.smartmask.MaskType;
import io.github.christianklisch.smartmask.annotations.Sensitive;
import io.github.christianklisch.smartmask.security.SecurityContextProvider;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import java.io.IOException;

/**
 * Custom Jackson Serializer for fields annotated with {@link Sensitive}.
 * <p>
 * This serializer is responsible for applying masking to sensitive data during JSON serialization.
 * It uses the configuration from the {@link Sensitive} annotation to determine how to mask the data,
 * and checks the current user's roles via the {@link SecurityContextProvider} to determine if
 * masking should be applied.
 * </p>
 * <p>
 * The serializer supports different masking strategies based on the {@link MaskType} specified
 * in the annotation, including specialized masking for emails, credit cards, phone numbers, and IBANs.
 * </p>
 * <p>
 * This class is not directly instantiated by users. Instead, it is created by the
 * {@link SensitiveAnnotationIntrospector} when it detects a field annotated with {@link Sensitive}.
 * </p>
 * 
 * @see Sensitive
 * @see MaskType
 * @see SecurityContextProvider
 * @see SensitiveAnnotationIntrospector
 */
public class SensitiveDataSerializer extends JsonSerializer<Object> {

    // Dependency to access the SecurityContext
    private final SecurityContextProvider securityContextProvider;
    private final Sensitive sensitiveAnnotation; // The specific annotation for this field

    /**
     * Constructor for the serializer.
     * <p>
     * Jackson instantiates serializers via the default constructor, but we need to pass
     * the annotation info and security context provider dynamically at runtime.
     * This is done via the {@link SensitiveAnnotationIntrospector}.
     * </p>
     *
     * @param sensitiveAnnotation The {@link Sensitive} annotation from the field
     * @param securityContextProvider The security context provider for role checks
     */
    public SensitiveDataSerializer(@NonNull Sensitive sensitiveAnnotation, 
                                  @NonNull SecurityContextProvider securityContextProvider) {
        this.sensitiveAnnotation = sensitiveAnnotation;
        this.securityContextProvider = securityContextProvider;
    }

    /**
     * Serializes the value, applying masking if necessary based on the user's roles.
     * <p>
     * This method is called by Jackson during JSON serialization for each field
     * that has been configured to use this serializer.
     * </p>
     * <p>
     * The method first checks if the current user has any of the roles specified
     * in the {@link Sensitive#rolesAllowed()} attribute. If so, the value is serialized
     * without masking. Otherwise, the value is masked according to the configuration
     * in the {@link Sensitive} annotation.
     * </p>
     *
     * @param value The value to serialize
     * @param gen The JSON generator
     * @param serializers The serializer provider
     * @throws IOException If an I/O error occurs during serialization
     */
    @Override
    public void serialize(@Nullable Object value, @NonNull JsonGenerator gen, 
                         @NonNull SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        // 1. Role check
        boolean isAuthorized = securityContextProvider.hasAnyRole(sensitiveAnnotation.rolesAllowed());

        if (isAuthorized) {
            // User has permission to see the unmasked value
            gen.writeObject(value);
        } else {
            // Mask the value
            String maskedValue = mask(value.toString(), sensitiveAnnotation);
            gen.writeString(maskedValue);
        }
    }

    /**
     * Applies the appropriate masking strategy based on the annotation's configuration.
     * <p>
     * This method delegates to the specific masking method based on the {@link MaskType}
     * specified in the annotation.
     * </p>
     *
     * @param value The string value to mask
     * @param annotation The {@link Sensitive} annotation containing the masking configuration
     * @return The masked string
     */
    @NonNull
    private String mask(@Nullable String value, @NonNull Sensitive annotation) {
        if (value == null || value.isEmpty()) {
            return value != null ? value : "";
        }

        switch (annotation.type()) {
            case EMAIL:
                return maskEmail(value, annotation.maskChar());
            case CREDIT_CARD:
                return maskCreditCard(value, annotation.maskChar());
            case PHONE_NUMBER:
                return maskPhoneNumber(value, annotation.maskChar());
            case IBAN:
                return maskIban(value, annotation.maskChar());
            case GENERIC:
            default:
                return maskGeneric(value, annotation.showFirst(), annotation.showLast(), annotation.maskChar());
        }
    }

    /**
     * Applies generic masking to a string value.
     * <p>
     * Shows a specified number of characters from the start and/or end of the string,
     * replacing the rest with the mask character.
     * </p>
     * <p>
     * If the string is shorter than or equal to the sum of showFirst and showLast,
     * the entire string is masked.
     * </p>
     *
     * @param value The string to mask
     * @param showFirst Number of characters to show from the start
     * @param showLast Number of characters to show from the end
     * @param maskChar The character to use for masking
     * @return The masked string
     */
    @NonNull
    private String maskGeneric(@NonNull String value, int showFirst, int showLast, char maskChar) {
        if (value.length() <= showFirst + showLast) {
            return createMaskString(maskChar, value.length()); // Mask the entire value
        }

        StringBuilder sb = new StringBuilder();
        sb.append(value.substring(0, showFirst));
        sb.append(createMaskString(maskChar, value.length() - showFirst - showLast));
        sb.append(value.substring(value.length() - showLast));
        return sb.toString();
    }

    /**
     * Creates a string of mask characters with the specified length.
     *
     * @param maskChar The character to use for masking
     * @param length The length of the mask string
     * @return A string consisting of the mask character repeated length times
     */
    @NonNull
    private String createMaskString(char maskChar, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(maskChar);
        }
        return sb.toString();
    }

    /**
     * Applies email-specific masking.
     * <p>
     * Shows the first and last character of the local part (before the @)
     * and preserves the domain part (after the @).
     * </p>
     * <p>
     * Example: "user@example.com" becomes "u***r@example.com"
     * </p>
     *
     * @param email The email address to mask
     * @param maskChar The character to use for masking
     * @return The masked email address
     */
    @NonNull
    private String maskEmail(@NonNull String email, char maskChar) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) return maskGeneric(email, 0, 0, maskChar); // Not a valid email format
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        // Mask local part, show first and last character
        String maskedLocalPart = maskGeneric(localPart, 1, 1, maskChar);
        return maskedLocalPart + domainPart;
    }

    /**
     * Applies credit card-specific masking.
     * <p>
     * Removes any non-digit characters and shows only the last 4 digits,
     * masking all other digits.
     * </p>
     * <p>
     * Example: "4111-1111-1111-1111" becomes "************1111"
     * </p>
     *
     * @param ccNumber The credit card number to mask
     * @param maskChar The character to use for masking
     * @return The masked credit card number
     */
    @NonNull
    private String maskCreditCard(@NonNull String ccNumber, char maskChar) {
        String digitsOnly = ccNumber.replaceAll("[^\\d]", "");
        if (digitsOnly.length() < 12) { // At least 12 digits for meaningful masking
            return maskGeneric(ccNumber, 0, 0, maskChar);
        }
        // Show the last 4 digits
        return createMaskString(maskChar, digitsOnly.length() - 4) + digitsOnly.substring(digitsOnly.length() - 4);
    }

    /**
     * Applies phone number-specific masking.
     * <p>
     * Removes any non-digit characters and shows the first 3 and last 2 digits,
     * masking all other digits.
     * </p>
     * <p>
     * Example: "+1 (555) 123-4567" becomes "555*****67"
     * </p>
     *
     * @param phoneNumber The phone number to mask
     * @param maskChar The character to use for masking
     * @return The masked phone number
     */
    @NonNull
    private String maskPhoneNumber(@NonNull String phoneNumber, char maskChar) {
        String digitsOnly = phoneNumber.replaceAll("[^\\d]", "");
        if (digitsOnly.length() < 7) { // At least 7 digits for meaningful masking
            return maskGeneric(phoneNumber, 0, 0, maskChar);
        }
        // Show e.g. the first 3 and last 2 digits
        return maskGeneric(digitsOnly, 3, 2, maskChar);
    }

    /**
     * Applies IBAN-specific masking.
     * <p>
     * Removes any non-alphanumeric characters and shows the first 4 and last 4 characters,
     * masking all other characters.
     * </p>
     * <p>
     * Example: "DE89 3704 0044 0532 0130 00" becomes "DE89**********0000"
     * </p>
     *
     * @param iban The IBAN to mask
     * @param maskChar The character to use for masking
     * @return The masked IBAN
     */
    @NonNull
    private String maskIban(@NonNull String iban, char maskChar) {
        String alphaNumericOnly = iban.replaceAll("[^a-zA-Z0-9]", "");
        if (alphaNumericOnly.length() < 8) { // At least 8 characters for meaningful masking
            return maskGeneric(iban, 0, 0, maskChar);
        }
        // Show the first 4 and last 4 characters
        return maskGeneric(alphaNumericOnly, 4, 4, maskChar);
    }
}
