package io.github.christianklisch.smartmask.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.christianklisch.smartmask.MaskType;
import io.github.christianklisch.smartmask.annotations.Sensitive;
import io.github.christianklisch.smartmask.security.SecurityContextProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// This test demonstrates how to use the @Sensitive annotation on a DTO
// and verifies that the sensitive fields are masked during JSON serialization.
class SensitiveDataSerializerCreditcardTest {

    // Sample DTO using the @Sensitive annotation for fields that need masking.
    static class UserDto {

        private final String email;

        @Sensitive(type = MaskType.CREDIT_CARD, showLast = 4)
        private final String creditCardNumber;

        // Regular field, should not be masked
        private final String username;

        UserDto(
                @JsonProperty("email") String email,
                @JsonProperty("creditCardNumber") String creditCardNumber,
                @JsonProperty("username") String username) {
            this.email = email;
            this.creditCardNumber = creditCardNumber;
            this.username = username;
        }

        public String getEmail() { return email; }

        public String getCreditCardNumber() { return creditCardNumber; }

        public String getUsername() { return username; }
    }

    @Test
    void fields_annotated_with_Sensitive_are_masked_on_serialization() throws Exception {
        // Arrange: Build a UserDto with sensitive information
        UserDto user = new UserDto("john.doe@example.com", "4111111111111234", "johnny");

        // Set up Jackson ObjectMapper with custom SensitiveAnnotationIntrospector
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        // Register the introspector; usually done via Spring, for test we do it manually
        SensitiveAnnotationIntrospector introspector = new SensitiveAnnotationIntrospector();
        introspector.setSecurityContextProvider(new SecurityContextProvider()); // default/no roles
        mapper.setAnnotationIntrospector(introspector);

        // Act: Serialize the DTO to JSON
        String serialized = mapper.writeValueAsString(user);

        // Assert: The credit card number should be masked, but username should not be altered
        // Example: should contain "creditCardNumber":"************1234"
        assertThat(serialized)
                .contains("\"creditCardNumber\":\"************1234\"")
                .contains("\"username\":\"johnny\"")
                .contains("\"email\":\"john.doe@example.com\""); // not annotated, so unmasked
    }
}