package io.github.christianklisch.smartmask;

import org.springframework.lang.NonNull;

/**
 * Enum defining different types of masking strategies for sensitive data.
 * <p>
 * Each masking type implements a specific algorithm optimized for the data type
 * it's designed to mask, ensuring that sensitive information is properly protected
 * while maintaining a format that indicates the type of data being masked.
 * </p>
 * 
 * @see io.github.christianklisch.smartmask.annotations.Sensitive
 * @see io.github.christianklisch.smartmask.jackson.SensitiveDataSerializer
 */
public enum MaskType {
    /**
     * Generic masking strategy that can be customized with showFirst, showLast, and maskChar.
     * <p>
     * This is the default masking type and can be used for any kind of string data.
     * </p>
     * <p>
     * Example: With showFirst=2, showLast=2, maskChar='*'<br>
     * "password123" becomes "pa******23"
     * </p>
     */
    GENERIC,

    /**
     * Specialized masking for email addresses, preserving format.
     * <p>
     * Shows the first and last character of the local part and preserves the domain.
     * </p>
     * <p>
     * Example: "user@example.com" becomes "u***r@example.com"
     * </p>
     */
    EMAIL,

    /**
     * Specialized masking for credit card numbers, typically showing only last 4 digits.
     * <p>
     * Removes any non-digit characters before masking.
     * </p>
     * <p>
     * Example: "4111-1111-1111-1111" becomes "************1111"
     * </p>
     */
    CREDIT_CARD,

    /**
     * Specialized masking for phone numbers.
     * <p>
     * Shows the first 3 and last 2 digits, masking everything in between.
     * Removes any non-digit characters before masking.
     * </p>
     * <p>
     * Example: "+1 (555) 123-4567" becomes "555*****67"
     * </p>
     */
    PHONE_NUMBER,

    /**
     * Specialized masking for International Bank Account Numbers.
     * <p>
     * Shows the first 4 and last 4 characters, masking everything in between.
     * Removes any non-alphanumeric characters before masking.
     * </p>
     * <p>
     * Example: "DE89 3704 0044 0532 0130 00" becomes "DE89**********0000"
     * </p>
     */
    IBAN
}
