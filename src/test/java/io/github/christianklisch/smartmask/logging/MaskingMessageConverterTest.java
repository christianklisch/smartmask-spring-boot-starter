package io.github.christianklisch.smartmask.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.christianklisch.smartmask.annotations.Sensitive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the MaskingMessageConverter class.
 * These tests verify that the converter correctly masks sensitive data in log messages.
 */
@ExtendWith(MockitoExtension.class)
public class MaskingMessageConverterTest {

    @Mock
    private ILoggingEvent loggingEvent;

    private MaskingMessageConverter converter;

    @BeforeEach
    void setUp() {
        converter = new MaskingMessageConverter();
    }

    /**
     * Test that messages without arguments are not modified.
     */
    @Test
    void testConvertWithoutArguments() {
        // Arrange
        String message = "This is a log message without arguments";
        when(loggingEvent.getFormattedMessage()).thenReturn(message);
        when(loggingEvent.getArgumentArray()).thenReturn(null);

        // Act
        String result = converter.convert(loggingEvent);

        // Assert
        assertEquals(message, result);
    }

    /**
     * Test that primitive arguments are not masked.
     */
    @Test
    void testConvertWithPrimitiveArguments() {
        // Arrange
        String message = "Value: {}";
        Object[] args = new Object[] { 42 };
        when(loggingEvent.getMessage()).thenReturn(message);
        when(loggingEvent.getArgumentArray()).thenReturn(args);

        // Act
        String result = converter.convert(loggingEvent);

        // Assert
        assertEquals("Value: 42", result);
    }

    /**
     * Test that string arguments are not masked.
     */
    @Test
    void testConvertWithStringArguments() {
        // Arrange
        String message = "Message: {}";
        Object[] args = new Object[] { "Hello World" };
        when(loggingEvent.getMessage()).thenReturn(message);
        when(loggingEvent.getArgumentArray()).thenReturn(args);

        // Act
        String result = converter.convert(loggingEvent);

        // Assert
        assertEquals("Message: Hello World", result);
    }

    /**
     * Test that objects with sensitive fields are masked.
     */
    @Test
    void testConvertWithSensitiveObject() {
        // Arrange
        String message = "User: {}";
        Object[] args = new Object[] { new UserWithSensitiveData() };
        when(loggingEvent.getMessage()).thenReturn(message);
        when(loggingEvent.getArgumentArray()).thenReturn(args);

        // Act
        String result = converter.convert(loggingEvent);

        // Assert
        assertTrue(result.contains("UserWithSensitiveData"));
        assertTrue(result.contains("username='testuser'"));
        assertTrue(result.contains("password='******'"));
        assertFalse(result.contains("secret123"));
    }

    /**
     * Test that multiple arguments are correctly processed.
     */
    @Test
    void testConvertWithMultipleArguments() {
        // Arrange
        String message = "User: {}, Count: {}, Message: {}";
        Object[] args = new Object[] { 
            new UserWithSensitiveData(), 
            42, 
            "Hello World" 
        };
        when(loggingEvent.getMessage()).thenReturn(message);
        when(loggingEvent.getArgumentArray()).thenReturn(args);

        // Act
        String result = converter.convert(loggingEvent);

        // Assert
        assertTrue(result.contains("UserWithSensitiveData"));
        assertTrue(result.contains("username='testuser'"));
        assertTrue(result.contains("password='******'"));
        assertFalse(result.contains("secret123"));
        assertTrue(result.contains("42"));
        assertTrue(result.contains("Hello World"));
    }

    /**
     * Test class with sensitive data for testing purposes.
     */
    static class UserWithSensitiveData {
        @Sensitive
        private String password = "secret123";

        private String username = "testuser";

        @Override
        public String toString() {
            return "UserWithSensitiveData{username='" + username + "', password='" + password + "'}";
        }
    }
}
