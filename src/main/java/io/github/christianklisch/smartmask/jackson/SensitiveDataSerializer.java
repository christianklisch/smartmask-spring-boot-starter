package io.github.christianklisch.smartmask.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.christianklisch.smartmask.MaskType;
import io.github.christianklisch.smartmask.annotations.Sensitive;
import io.github.christianklisch.smartmask.security.SecurityContextProvider;
import java.io.IOException;

/**
 * Custom Jackson Serializer for fields annotated with @Sensitive.
 * It applies masking based on the annotation's configuration and current user roles.
 */
public class SensitiveDataSerializer extends JsonSerializer<Object> {

    // Dependency to access the SecurityContext
    private final SecurityContextProvider securityContextProvider;
    private final Sensitive sensitiveAnnotation; // The specific annotation for this field

    /**
     * Constructor for the serializer.
     * Jackson instantiates serializers via the default constructor.
     * We need to pass the annotation info dynamically at runtime.
     * This is done via the SensitiveAnnotationIntrospector.
     *
     * @param sensitiveAnnotation The @Sensitive annotation from the field
     * @param securityContextProvider The security context provider for role checks
     */
    public SensitiveDataSerializer(Sensitive sensitiveAnnotation, SecurityContextProvider securityContextProvider) {
        this.sensitiveAnnotation = sensitiveAnnotation;
        this.securityContextProvider = securityContextProvider;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
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

    private String mask(String value, Sensitive annotation) {
        if (value == null || value.isEmpty()) {
            return value;
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

    private String maskGeneric(String value, int showFirst, int showLast, char maskChar) {
        if (value.length() <= showFirst + showLast) {
            return createMaskString(maskChar, value.length()); // Mask the entire value
        }

        StringBuilder sb = new StringBuilder();
        sb.append(value.substring(0, showFirst));
        sb.append(createMaskString(maskChar, value.length() - showFirst - showLast));
        sb.append(value.substring(value.length() - showLast));
        return sb.toString();
    }

    private String createMaskString(char maskChar, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(maskChar);
        }
        return sb.toString();
    }

    private String maskEmail(String email, char maskChar) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) return maskGeneric(email, 0, 0, maskChar); // Not a valid email format
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        // Mask local part, show first and last character
        String maskedLocalPart = maskGeneric(localPart, 1, 1, maskChar);
        return maskedLocalPart + domainPart;
    }

    private String maskCreditCard(String ccNumber, char maskChar) {
        String digitsOnly = ccNumber.replaceAll("[^\\d]", "");
        if (digitsOnly.length() < 12) { // At least 12 digits for meaningful masking
            return maskGeneric(ccNumber, 0, 0, maskChar);
        }
        // Show the last 4 digits
        return createMaskString(maskChar, digitsOnly.length() - 4) + digitsOnly.substring(digitsOnly.length() - 4);
    }

    private String maskPhoneNumber(String phoneNumber, char maskChar) {
        String digitsOnly = phoneNumber.replaceAll("[^\\d]", "");
        if (digitsOnly.length() < 7) { // At least 7 digits for meaningful masking
            return maskGeneric(phoneNumber, 0, 0, maskChar);
        }
        // Show e.g. the first 3 and last 2 digits
        return maskGeneric(digitsOnly, 3, 2, maskChar);
    }

    private String maskIban(String iban, char maskChar) {
        String alphaNumericOnly = iban.replaceAll("[^a-zA-Z0-9]", "");
        if (alphaNumericOnly.length() < 8) { // At least 8 characters for meaningful masking
            return maskGeneric(iban, 0, 0, maskChar);
        }
        // Show the first 4 and last 4 characters
        return maskGeneric(alphaNumericOnly, 4, 4, maskChar);
    }
}
