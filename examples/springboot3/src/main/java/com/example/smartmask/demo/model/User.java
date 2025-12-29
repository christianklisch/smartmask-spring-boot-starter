package com.example.smartmask.demo.model;

import io.github.christianklisch.smartmask.MaskType;
import io.github.christianklisch.smartmask.annotations.Sensitive;

/**
 * User model class that demonstrates various ways to use the {@code @Sensitive} annotation
 * for masking sensitive data in both JSON responses and logs.
 * <p>
 * This class contains multiple fields annotated with {@code @Sensitive}, each demonstrating
 * a different masking strategy. When instances of this class are serialized to JSON or
 * logged, the sensitive fields are automatically masked according to their masking rules.
 * </p>
 * <p>
 * The masking works in two ways:
 * </p>
 * <ol>
 *   <li><strong>JSON Serialization</strong>: When a User object is returned from a REST controller,
 *       the Jackson serializer uses the {@code SensitiveDataSerializer} to mask sensitive fields.</li>
 *   <li><strong>Logging</strong>: When a User object is logged using SLF4J, the {@code MaskingMessageConverter}
 *       intercepts the log message and masks sensitive fields before they appear in logs.</li>
 * </ol>
 */
public class User {
    private Long id;
    private String username;

    /**
     * Password field with default masking.
     * <p>
     * When no specific masking type is provided, {@code MaskType.GENERIC} is used by default,
     * which replaces the entire value with asterisks (*).
     * </p>
     * <p>
     * Example: "secret123" becomes "******"
     * </p>
     */
    @Sensitive
    private String password;

    /**
     * Email field with email-specific masking.
     * <p>
     * {@code MaskType.EMAIL} preserves the first character, the domain part, and masks
     * everything in between with asterisks.
     * </p>
     * <p>
     * Example: "john.doe@example.com" becomes "j******e@example.com"
     * </p>
     */
    @Sensitive(type = MaskType.EMAIL)
    private String email;

    /**
     * Credit card field with credit card-specific masking.
     * <p>
     * {@code MaskType.CREDIT_CARD} preserves the last 4 digits and masks the rest with asterisks.
     * </p>
     * <p>
     * Example: "4111111111111111" becomes "************1111"
     * </p>
     */
    @Sensitive(type = MaskType.CREDIT_CARD)
    private String creditCardNumber;

    /**
     * Phone number field with phone-specific masking.
     * <p>
     * {@code MaskType.PHONE_NUMBER} preserves the last 5 digits and masks the rest with asterisks.
     * </p>
     * <p>
     * Example: "12345678901" becomes "*****78901"
     * </p>
     */
    @Sensitive(type = MaskType.PHONE_NUMBER)
    private String phoneNumber;

    /**
     * IBAN field with IBAN-specific masking.
     * <p>
     * {@code MaskType.IBAN} preserves the country code and the last 4 digits, masking everything else.
     * </p>
     * <p>
     * Example: "DE89370400440532013000" becomes "DE89********3000"
     * </p>
     */
    @Sensitive(type = MaskType.IBAN)
    private String iban;

    /**
     * Field demonstrating partial visibility by showing the first N characters.
     * <p>
     * The {@code showFirst} parameter specifies how many characters from the beginning
     * should remain visible, while the rest are masked.
     * </p>
     * <p>
     * Example: "SENSITIVE_DATA_123" with showFirst=3 becomes "SEN***************"
     * </p>
     */
    @Sensitive(showFirst = 3)
    private String partiallyVisibleData;

    /**
     * Field demonstrating partial visibility by showing the last N characters.
     * <p>
     * The {@code showLast} parameter specifies how many characters from the end
     * should remain visible, while the rest are masked.
     * </p>
     * <p>
     * Example: "SENSITIVE_DATA_456" with showLast=4 becomes "****************456"
     * </p>
     */
    @Sensitive(showLast = 4)
    private String lastFourVisible;

    /**
     * Field demonstrating a custom mask character.
     * <p>
     * The {@code maskChar} parameter specifies which character should be used for masking
     * instead of the default asterisk (*).
     * </p>
     * <p>
     * Example: "CUSTOM_MASK_CHAR_DATA" with maskChar='#' becomes "######################"
     * </p>
     */
    @Sensitive(maskChar = '#')
    private String customMaskChar;

    /**
     * Field demonstrating role-based access control for sensitive data.
     * <p>
     * The {@code rolesAllowed} parameter specifies which roles are allowed to see the
     * unmasked value. Users without these roles will see the masked value.
     * </p>
     * <p>
     * Example: "This data is only visible to admins" is only visible to users with ROLE_ADMIN,
     * otherwise it's masked as "********************************"
     * </p>
     */
    @Sensitive(rolesAllowed = {"ROLE_ADMIN"})
    private String adminOnlyData;

    // Constructors
    public User() {
    }

    public User(Long id, String username, String password, String email, String creditCardNumber, 
                String phoneNumber, String iban, String partiallyVisibleData, String lastFourVisible, 
                String customMaskChar, String adminOnlyData) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.creditCardNumber = creditCardNumber;
        this.phoneNumber = phoneNumber;
        this.iban = iban;
        this.partiallyVisibleData = partiallyVisibleData;
        this.lastFourVisible = lastFourVisible;
        this.customMaskChar = customMaskChar;
        this.adminOnlyData = adminOnlyData;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getPartiallyVisibleData() {
        return partiallyVisibleData;
    }

    public void setPartiallyVisibleData(String partiallyVisibleData) {
        this.partiallyVisibleData = partiallyVisibleData;
    }

    public String getLastFourVisible() {
        return lastFourVisible;
    }

    public void setLastFourVisible(String lastFourVisible) {
        this.lastFourVisible = lastFourVisible;
    }

    public String getCustomMaskChar() {
        return customMaskChar;
    }

    public void setCustomMaskChar(String customMaskChar) {
        this.customMaskChar = customMaskChar;
    }

    public String getAdminOnlyData() {
        return adminOnlyData;
    }

    public void setAdminOnlyData(String adminOnlyData) {
        this.adminOnlyData = adminOnlyData;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", creditCardNumber='" + creditCardNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", iban='" + iban + '\'' +
                ", partiallyVisibleData='" + partiallyVisibleData + '\'' +
                ", lastFourVisible='" + lastFourVisible + '\'' +
                ", customMaskChar='" + customMaskChar + '\'' +
                ", adminOnlyData='" + adminOnlyData + '\'' +
                '}';
    }
}
