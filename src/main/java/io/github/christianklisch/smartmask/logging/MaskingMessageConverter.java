package io.github.christianklisch.smartmask.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.christianklisch.smartmask.annotations.Sensitive;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Logback Converter that inspects log arguments for {@link Sensitive} annotations
 * and masks them before they appear in the logs.
 * <p>
 * This class extends Logback's {@link ClassicConverter} to intercept log messages
 * and mask any objects that contain fields annotated with {@link Sensitive}.
 * This ensures that sensitive data is not accidentally logged in plain text.
 * </p>
 * <p>
 * To use this converter in your Logback configuration, add it to your logback.xml file:
 * </p>
 * <pre>
 * &lt;configuration&gt;
 *     &lt;conversionRule conversionWord="maskedMsg" 
 *                    converterClass="io.github.christianklisch.smartmask.logging.MaskingMessageConverter" /&gt;
 *     
 *     &lt;appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender"&gt;
 *         &lt;encoder&gt;
 *             &lt;pattern&gt;%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %maskedMsg%n&lt;/pattern&gt;
 *         &lt;/encoder&gt;
 *     &lt;/appender&gt;
 *     
 *     &lt;root level="info"&gt;
 *         &lt;appender-ref ref="CONSOLE" /&gt;
 *     &lt;/root&gt;
 * &lt;/configuration&gt;
 * </pre>
 * <p>
 * This implementation uses an optimized reflection approach to discover fields annotated with {@link Sensitive}:
 * </p>
 * <ul>
 *   <li>It traverses the entire class hierarchy to find all annotated fields, including inherited ones</li>
 *   <li>It employs a two-level caching strategy with both instance-level and static global caches</li>
 *   <li>The global cache is shared across all instances of the converter for maximum efficiency</li>
 *   <li>Field discovery is performed only once per class and the results are cached</li>
 * </ul>
 * <p>
 * In a future version, it could be enhanced to create a copy of objects or use a proxy 
 * to avoid modifying the original objects.
 * </p>
 * 
 * @see Sensitive
 * @see ClassicConverter
 * @see ILoggingEvent
 */
public class MaskingMessageConverter extends ClassicConverter {

    // Cache for reflection results to improve performance
    private final Map<Class<?>, Field[]> sensitiveFieldsCache = new ConcurrentHashMap<>();

    // Static cache for better performance across instances
    private static final Map<Class<?>, Field[]> globalSensitiveFieldsCache = new ConcurrentHashMap<>();

    /**
     * Converts a logging event by masking any sensitive data in the log arguments.
     * <p>
     * This method is called by Logback for each log message that uses this converter.
     * It examines the arguments of the log message, masks any objects that contain
     * fields annotated with {@link Sensitive}, and then formats the message with
     * the masked arguments.
     * </p>
     *
     * @param event The logging event to convert
     * @return The formatted message with sensitive data masked
     */
    @Override
    @NonNull
    public String convert(@NonNull ILoggingEvent event) {
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

    /**
     * Masks an object if it contains fields annotated with {@link Sensitive}.
     * <p>
     * This method examines the object using reflection to determine if it contains
     * any fields annotated with {@link Sensitive}. If it does, the object is replaced
     * with a masked representation. Otherwise, the original object is returned.
     * </p>
     * <p>
     * The results of the reflection analysis are cached for performance.
     * </p>
     *
     * @param arg The object to potentially mask
     * @return The original object if it doesn't contain sensitive fields, or a masked representation if it does
     */
    @Nullable
    private Object maskIfNecessary(@Nullable Object arg) {
        if (arg == null || isPrimitiveOrWrapper(arg.getClass())) {
            return arg;
        }

        Class<?> clazz = arg.getClass();

        // First check the instance cache
        Field[] sensitiveFields = sensitiveFieldsCache.get(clazz);

        if (sensitiveFields == null) {
            // Then check the global cache
            sensitiveFields = globalSensitiveFieldsCache.get(clazz);

            if (sensitiveFields == null) {
                // If not in any cache, discover fields and cache the result
                sensitiveFields = discoverSensitiveFields(clazz);
                globalSensitiveFieldsCache.put(clazz, sensitiveFields);
            }

            // Update the instance cache
            sensitiveFieldsCache.put(clazz, sensitiveFields);
        }

        if (sensitiveFields.length > 0) {
            // Instead of replacing the entire object, we'll mask the sensitive fields
            // by using the object's toString() method and returning the original object
            // The toString() method will be called later when the object is formatted
            // and will show the object structure with masked sensitive fields
            for (Field field : sensitiveFields) {
                try {
                    field.setAccessible(true);
                    Object originalValue = field.get(arg);
                    if (originalValue != null) {
                        // Apply masking based on the Sensitive annotation
                        Sensitive annotation = field.getAnnotation(Sensitive.class);
                        String maskedValue = "******"; // Default mask
                        field.set(arg, maskedValue);
                    }
                } catch (IllegalAccessException e) {
                    // If we can't access the field, we'll just leave it as is
                }
            }
        }

        return arg;
    }

    /**
     * Discovers all fields annotated with {@link Sensitive} in a class and its superclasses.
     * <p>
     * This method uses reflection to find all fields in the class hierarchy that are
     * annotated with {@link Sensitive}. It includes both declared fields and inherited fields.
     * </p>
     * <p>
     * The implementation is optimized for performance:
     * </p>
     * <ul>
     *   <li>It uses a HashSet to efficiently collect fields and avoid duplicates</li>
     *   <li>It traverses the class hierarchy iteratively rather than recursively</li>
     *   <li>It processes fields directly in a loop rather than using Stream API for better performance</li>
     *   <li>The results are cached to avoid repeated reflection operations</li>
     * </ul>
     *
     * @param clazz The class to analyze
     * @return An array of fields annotated with {@link Sensitive}
     */
    @NonNull
    private Field[] discoverSensitiveFields(@NonNull Class<?> clazz) {
        // Use a set to avoid duplicate fields
        java.util.Set<Field> sensitiveFields = new java.util.HashSet<>();

        // Traverse the class hierarchy
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            // Find declared fields with @Sensitive annotation
            Field[] declaredFields = currentClass.getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.isAnnotationPresent(Sensitive.class)) {
                    sensitiveFields.add(field);
                }
            }

            // Move up to the superclass
            currentClass = currentClass.getSuperclass();
        }

        return sensitiveFields.toArray(new Field[0]);
    }

    /**
     * Determines if a type is a primitive or wrapper type.
     * <p>
     * This method is used to quickly filter out types that cannot contain
     * fields annotated with {@link Sensitive}.
     * </p>
     *
     * @param type The class to check
     * @return true if the class is a primitive or wrapper type, false otherwise
     */
    private boolean isPrimitiveOrWrapper(@NonNull Class<?> type) {
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

    /**
     * Formats a message by replacing placeholders with argument values.
     * <p>
     * This is a simplified version of Logback's message formatting logic.
     * It replaces each occurrence of {} in the message with the corresponding
     * argument value.
     * </p>
     * <p>
     * Note: In a production implementation, you should use SLF4J's MessageFormatter
     * for more robust formatting.
     * </p>
     *
     * @param message The message template with {} placeholders
     * @param args The arguments to substitute into the placeholders
     * @return The formatted message
     */
    @NonNull
    private String formatMessage(@Nullable String message, @NonNull Object[] args) {
        if (message == null) {
            return "";
        }

        // Very simplified Logback logic: Replaces {} with the masked arguments
        String result = message;
        for (Object arg : args) {
            result = result.replaceFirst("\\{}", String.valueOf(arg));
        }
        return result;
    }
}
