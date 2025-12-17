package io.github.christianklisch.smartmask;

/**
 * Enum defining different types of masking strategies for sensitive data.
 */
public enum MaskType {
    /**
     * Generic masking strategy that can be customized with showFirst, showLast, and maskChar.
     */
    GENERIC,
    
    /**
     * Specialized masking for email addresses, preserving format.
     */
    EMAIL,
    
    /**
     * Specialized masking for credit card numbers, typically showing only last 4 digits.
     */
    CREDIT_CARD,
    
    /**
     * Specialized masking for phone numbers.
     */
    PHONE_NUMBER,
    
    /**
     * Specialized masking for International Bank Account Numbers.
     */
    IBAN
}