package io.github.christianklisch.smartmask.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.christianklisch.smartmask.MaskType;
import io.github.christianklisch.smartmask.annotations.Sensitive;
import io.github.christianklisch.smartmask.security.SecurityContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the SensitiveDataSerializer class.
 * These tests verify that the serializer correctly masks different types of sensitive data
 * based on the configuration in the @Sensitive annotation and the user's roles.
 */
@ExtendWith(MockitoExtension.class)
public class SensitiveDataSerializerTest {

    @Mock
    private SecurityContextProvider securityContextProvider;

    @Mock
    private Sensitive sensitiveAnnotation;

    private SensitiveDataSerializer serializer;
    private ObjectMapper objectMapper;
    private JsonGenerator jsonGenerator;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws IOException {
        // Set up default behavior for the mocked annotation
        lenient().when(sensitiveAnnotation.type()).thenReturn(MaskType.GENERIC);
        lenient().when(sensitiveAnnotation.showFirst()).thenReturn(0);
        lenient().when(sensitiveAnnotation.showLast()).thenReturn(0);
        lenient().when(sensitiveAnnotation.maskChar()).thenReturn('*');
        lenient().when(sensitiveAnnotation.rolesAllowed()).thenReturn(new String[0]);

        // Set up the serializer with the mocked dependencies
        serializer = new SensitiveDataSerializer(sensitiveAnnotation, securityContextProvider);

        // Set up Jackson components for testing
        objectMapper = new ObjectMapper();
        stringWriter = new StringWriter();
        jsonGenerator = objectMapper.getFactory().createGenerator(stringWriter);
    }

    /**
     * Test that null values are serialized as null without masking.
     */
    @Test
    void testSerializeNull() throws IOException {
        // Arrange
        SerializerProvider serializerProvider = objectMapper.getSerializerProvider();

        // Act
        serializer.serialize(null, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        // Assert
        assertEquals("null", stringWriter.toString());
    }

    /**
     * Test that values are not masked when the user has the required role.
     */
    @Test
    void testSerializeWithAuthorizedUser() throws IOException {
        // Arrange
        SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        when(securityContextProvider.hasAnyRole(any())).thenReturn(true);
        String value = "sensitive-data";

        // Act
        serializer.serialize(value, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        // Assert
        assertEquals("\"sensitive-data\"", stringWriter.toString());
    }

    /**
     * Test generic masking with default settings (all characters masked).
     */
    @Test
    void testGenericMaskingDefault() throws IOException {
        // Arrange
        SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        when(securityContextProvider.hasAnyRole(any())).thenReturn(false);
        String value = "password123";

        // Act
        serializer.serialize(value, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        // Assert
        assertEquals("\"***********\"", stringWriter.toString());
    }

    /**
     * Test generic masking with showFirst configuration.
     */
    @Test
    void testGenericMaskingShowFirst() throws IOException {
        // Arrange
        SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        when(securityContextProvider.hasAnyRole(any())).thenReturn(false);
        when(sensitiveAnnotation.showFirst()).thenReturn(3);
        String value = "password123";

        // Act
        serializer.serialize(value, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        // Assert
        assertEquals("\"pas********\"", stringWriter.toString());
    }

    /**
     * Test generic masking with showLast configuration.
     */
    @Test
    void testGenericMaskingShowLast() throws IOException {
        // Arrange
        SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        when(securityContextProvider.hasAnyRole(any())).thenReturn(false);
        when(sensitiveAnnotation.showLast()).thenReturn(4);
        String value = "1234567890";

        // Act
        serializer.serialize(value, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        // Assert
        assertEquals("\"******7890\"", stringWriter.toString());
    }

    /**
     * Test generic masking with both showFirst and showLast configuration.
     */
    @Test
    void testGenericMaskingShowFirstAndLast() throws IOException {
        // Arrange
        SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        when(securityContextProvider.hasAnyRole(any())).thenReturn(false);
        when(sensitiveAnnotation.showFirst()).thenReturn(2);
        when(sensitiveAnnotation.showLast()).thenReturn(2);
        String value = "1234567890";

        // Act
        serializer.serialize(value, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        // Assert
        assertEquals("\"12******90\"", stringWriter.toString());
    }

    /**
     * Test email masking.
     */
    @Test
    void testEmailMasking() throws IOException {
        // Arrange
        SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        when(securityContextProvider.hasAnyRole(any())).thenReturn(false);
        when(sensitiveAnnotation.type()).thenReturn(MaskType.EMAIL);
        String value = "user@example.com";

        // Act
        serializer.serialize(value, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        // Assert
        assertEquals("\"u**r@example.com\"", stringWriter.toString());
    }

    /**
     * Test credit card masking.
     */
    @Test
    void testCreditCardMasking() throws IOException {
        // Arrange
        SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        when(securityContextProvider.hasAnyRole(any())).thenReturn(false);
        when(sensitiveAnnotation.type()).thenReturn(MaskType.CREDIT_CARD);
        String value = "4111 1111 1111 1234";

        // Act
        serializer.serialize(value, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        // Assert
        assertEquals("\"************1234\"", stringWriter.toString());
    }

    /**
     * Test phone number masking.
     */
    @Test
    void testPhoneNumberMasking() throws IOException {
        // Arrange
        SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        when(securityContextProvider.hasAnyRole(any())).thenReturn(false);
        when(sensitiveAnnotation.type()).thenReturn(MaskType.PHONE_NUMBER);
        String value = "+1 (555) 123-4567";

        // Act
        serializer.serialize(value, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        // Assert
        assertEquals("\"155******67\"", stringWriter.toString());
    }

    /**
     * Test IBAN masking.
     */
    @Test
    void testIbanMasking() throws IOException {
        // Arrange
        SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        when(securityContextProvider.hasAnyRole(any())).thenReturn(false);
        when(sensitiveAnnotation.type()).thenReturn(MaskType.IBAN);
        String value = "DE89 3704 0044 0532 0130 00";

        // Act
        serializer.serialize(value, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        // Assert
        assertEquals("\"DE89**************3000\"", stringWriter.toString());
    }

    /**
     * Test custom mask character.
     */
    @Test
    void testCustomMaskChar() throws IOException {
        // Arrange
        SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        when(securityContextProvider.hasAnyRole(any())).thenReturn(false);
        when(sensitiveAnnotation.maskChar()).thenReturn('#');
        String value = "sensitive-data";

        // Act
        serializer.serialize(value, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        // Assert
        assertEquals("\"##############\"", stringWriter.toString());
    }
}