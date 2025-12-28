package com.example.smartmask.demo.model;

import io.github.christianklisch.smartmask.MaskType;
import io.github.christianklisch.smartmask.annotations.Sensitive;

public class User {
    private Long id;
    private String username;
    
    @Sensitive
    private String password;
    
    @Sensitive(type = MaskType.EMAIL)
    private String email;
    
    @Sensitive(type = MaskType.CREDIT_CARD)
    private String creditCardNumber;
    
    @Sensitive(type = MaskType.PHONE_NUMBER)
    private String phoneNumber;
    
    @Sensitive(type = MaskType.IBAN)
    private String iban;
    
    // Show first 3 characters, mask the rest
    @Sensitive(showFirst = 3)
    private String partiallyVisibleData;
    
    // Show last 4 characters, mask the rest
    @Sensitive(showLast = 4)
    private String lastFourVisible;
    
    // Use a custom mask character
    @Sensitive(maskChar = '#')
    private String customMaskChar;
    
    // Only users with ROLE_ADMIN can see the unmasked value
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