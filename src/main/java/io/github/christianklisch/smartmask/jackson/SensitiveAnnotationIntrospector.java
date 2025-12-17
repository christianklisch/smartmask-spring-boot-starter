package io.github.christianklisch.smartmask.jackson;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import io.github.christianklisch.smartmask.annotations.Sensitive;
import io.github.christianklisch.smartmask.security.SecurityContextProvider;

/**
 * Custom Jackson AnnotationIntrospector to register SensitiveDataSerializer
 * for fields annotated with @Sensitive.
 */
public class SensitiveAnnotationIntrospector extends JacksonAnnotationIntrospector {

    // CAUTION: Autowiring in Introspectors is tricky, as Jackson instantiates them itself.
    // This must be manually injected via a Spring bean in the ObjectMapper.
    private SecurityContextProvider securityContextProvider;

    /**
     * Default Constructor for Jackson
     */
    public SensitiveAnnotationIntrospector() {
        // Required for Jackson to instantiate
    }

    /**
     * Setter for Spring to inject SecurityContextProvider
     * This will be called if configured correctly in Spring.
     *
     * @param securityContextProvider The security context provider for role checks
     */
    public void setSecurityContextProvider(SecurityContextProvider securityContextProvider) {
        this.securityContextProvider = securityContextProvider;
    }

    @Override
    public Object findSerializer(Annotated a) {
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