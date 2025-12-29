package io.github.christianklisch.smartmask.jackson;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import io.github.christianklisch.smartmask.annotations.Sensitive;
import io.github.christianklisch.smartmask.security.SecurityContextProvider;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Custom Jackson AnnotationIntrospector that registers {@link SensitiveDataSerializer}
 * for fields annotated with {@link Sensitive}.
 * <p>
 * This class is a key component of the SmartMask library, as it integrates with Jackson's
 * serialization process to detect fields annotated with {@link Sensitive} and apply
 * the appropriate masking during JSON serialization.
 * </p>
 * <p>
 * The introspector is automatically configured by {@link io.github.christianklisch.smartmask.autoconfig.SensitiveDataMaskingAutoConfiguration}
 * and registered with the primary {@link com.fasterxml.jackson.databind.ObjectMapper} bean.
 * </p>
 * <p>
 * Note that this class requires a {@link SecurityContextProvider} to be injected,
 * which is used to check if the current user has the necessary roles to see unmasked
 * sensitive data. This injection is handled by the auto-configuration.
 * </p>
 * 
 * @see Sensitive
 * @see SensitiveDataSerializer
 * @see SecurityContextProvider
 * @see io.github.christianklisch.smartmask.autoconfig.SensitiveDataMaskingAutoConfiguration
 */
public class SensitiveAnnotationIntrospector extends JacksonAnnotationIntrospector {

    // CAUTION: Autowiring in Introspectors is tricky, as Jackson instantiates them itself.
    // This must be manually injected via a Spring bean in the ObjectMapper.
    private SecurityContextProvider securityContextProvider;

    /**
     * Default constructor required by Jackson.
     * <p>
     * Note that this constructor does not initialize the {@link SecurityContextProvider},
     * which must be set separately via {@link #setSecurityContextProvider(SecurityContextProvider)}.
     * This is handled automatically by the auto-configuration.
     * </p>
     */
    public SensitiveAnnotationIntrospector() {
        // Required for Jackson to instantiate
    }

    /**
     * Setter for injecting the {@link SecurityContextProvider}.
     * <p>
     * This method is called by the auto-configuration to inject the SecurityContextProvider
     * into the introspector. This is necessary because Jackson instantiates introspectors
     * itself, so standard dependency injection doesn't work.
     * </p>
     * <p>
     * This method must be called before the introspector is used, or sensitive data
     * will always be masked regardless of the user's roles.
     * </p>
     *
     * @param securityContextProvider The security context provider for role checks
     */
    public void setSecurityContextProvider(@NonNull SecurityContextProvider securityContextProvider) {
        this.securityContextProvider = securityContextProvider;
    }

    /**
     * Finds the appropriate serializer for a field or method.
     * <p>
     * This method is called by Jackson during serialization to determine if a custom
     * serializer should be used for a field or method. If the field or method is
     * annotated with {@link Sensitive}, this method returns a {@link SensitiveDataSerializer}
     * configured with the annotation and the {@link SecurityContextProvider}.
     * </p>
     * <p>
     * If the {@link SecurityContextProvider} has not been injected, a warning is logged
     * and a fallback instance is used, which will always mask sensitive data regardless
     * of the user's roles.
     * </p>
     *
     * @param a The annotated field or method
     * @return A {@link SensitiveDataSerializer} if the field or method is annotated with {@link Sensitive},
     *         otherwise the result of the superclass method
     */
    @Override
    @Nullable
    public Object findSerializer(@NonNull Annotated a) {
        Sensitive sensitiveAnnotation = a.getAnnotation(Sensitive.class);
        if (sensitiveAnnotation != null) {
            // IMPORTANT: Here we instantiate our serializer and pass the annotation
            // and the SecurityContextProvider.
            if (securityContextProvider == null) {
                // Fallback if SecurityContextProvider couldn't be injected
                // This shouldn't happen in a Spring Boot app if configured correctly.
                System.err.println("WARNING: SecurityContextProvider not injected into SensitiveAnnotationIntrospector. Sensitive data will always be masked.");
                return new SensitiveDataSerializer(sensitiveAnnotation, new SecurityContextProvider()); // Fallback instance
            }
            return new SensitiveDataSerializer(sensitiveAnnotation, securityContextProvider);
        }
        return super.findSerializer(a);
    }
}
