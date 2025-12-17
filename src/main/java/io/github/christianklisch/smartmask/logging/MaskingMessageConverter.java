package io.github.christianklisch.smartmask.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.christianklisch.smartmask.annotations.Sensitive;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Logback Converter that inspects log arguments for @Sensitive annotations
 * and masks them before they hit the logs.
 */
public class MaskingMessageConverter extends ClassicConverter {

    // Cache for reflection results to improve performance
    private final Map<Class<?>, Field[]> sensitiveFieldsCache = new ConcurrentHashMap<>();

    @Override
    public String convert(ILoggingEvent event) {
        Object[] argumentArray = event.getArgumentArray();
        if (argumentArray == null || argumentArray.length == 0) {
            return event.getFormattedMessage();
        }

        // Create a copy of the arguments to avoid permanently changing the original object
        Object[] maskedArguments = Stream.of(argumentArray)
                .map(this::maskIfNecessary)
                .toArray();

        // Logback uses MessageFormatter.arrayFormat internally, we simulate that here in a simplified way
        // For a production lib, you should use org.slf4j.helpers.MessageFormatter
        return formatMessage(event.getMessage(), maskedArguments);
    }

    private Object maskIfNecessary(Object arg) {
        if (arg == null || isPrimitiveOrWrapper(arg.getClass())) {
            return arg;
        }

        // Check if we've already analyzed this class
        Field[] sensitiveFields = sensitiveFieldsCache.computeIfAbsent(arg.getClass(), 
            clazz -> {
                // Find all fields with @Sensitive annotation
                return Stream.of(clazz.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Sensitive.class))
                    .toArray(Field[]::new);
            });

        if (sensitiveFields.length > 0) {
            // In a real implementation, we would create a copy of the object or
            // use a proxy to avoid corrupting the original.
            // For this example, we'll return a masked string representation.
            return "MaskedObject(" + arg.getClass().getSimpleName() + ")";
        }
        
        return arg;
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
               type == String.class ||
               type == Integer.class ||
               type == Long.class ||
               type == Boolean.class ||
               type == Double.class ||
               type == Float.class ||
               type == Short.class ||
               type == Byte.class ||
               type == Character.class;
    }

    private String formatMessage(String message, Object[] args) {
        // Very simplified Logback logic: Replaces {} with the masked arguments
        String result = message;
        for (Object arg : args) {
            result = result.replaceFirst("\\{}", String.valueOf(arg));
        }
        return result;
    }
}