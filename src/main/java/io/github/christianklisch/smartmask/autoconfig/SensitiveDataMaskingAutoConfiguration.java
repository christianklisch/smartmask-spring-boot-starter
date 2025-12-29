package io.github.christianklisch.smartmask.autoconfig;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.christianklisch.smartmask.jackson.SensitiveAnnotationIntrospector;
import io.github.christianklisch.smartmask.security.SecurityContextProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;

/**
 * Auto-configuration for the Sensitive Data Masking library.
 * <p>
 * This class is the entry point for Spring Boot auto-configuration. It automatically
 * registers all the necessary beans for the SmartMask library to function properly
 * when included in a Spring Boot application.
 * </p>
 * The auto-configuration:
 * <ul>
 *   <li>Creates a {@link SecurityContextProvider} bean if none exists</li>
 *   <li>Configures a {@link SensitiveAnnotationIntrospector} for Jackson</li>
 *   <li>Creates a Jackson module for sensitive data masking</li>
 *   <li>Provides a primary {@link ObjectMapper} configured for sensitive data masking</li>
 * </ul>
 * <p>
 * This auto-configuration is only activated if Jackson is available on the classpath
 * (specifically, if {@link ObjectMapper} and {@link Module} classes are present).
 * </p>
 * <p>
 * To use this library, simply add it as a dependency to your Spring Boot application,
 * and the auto-configuration will take care of the rest. Then you can use the
 * {@link io.github.christianklisch.smartmask.annotations.Sensitive} annotation
 * on your model classes to mark fields that should be masked.
 * </p>
 * 
 * @see io.github.christianklisch.smartmask.annotations.Sensitive
 * @see io.github.christianklisch.smartmask.jackson.SensitiveAnnotationIntrospector
 * @see io.github.christianklisch.smartmask.security.SecurityContextProvider
 */
@AutoConfiguration
@ConditionalOnClass({ObjectMapper.class, Module.class}) // Only activate if Jackson is available
public class SensitiveDataMaskingAutoConfiguration {

    /**
     * Creates a {@link SecurityContextProvider} bean if none exists in the application context.
     * <p>
     * The SecurityContextProvider is responsible for checking if the current user
     * has the necessary roles to see unmasked sensitive data.
     * </p>
     * <p>
     * Applications can override this bean by defining their own SecurityContextProvider
     * bean, which will be used instead of this default implementation.
     * </p>
     *
     * @return A new SecurityContextProvider if none exists
     */
    @Bean
    @ConditionalOnMissingBean
    @NonNull
    public SecurityContextProvider securityContextProvider() {
        return new SecurityContextProvider();
    }

    /**
     * Creates the {@link SensitiveAnnotationIntrospector} bean.
     * <p>
     * This introspector is responsible for detecting the {@link io.github.christianklisch.smartmask.annotations.Sensitive}
     * annotation on fields and methods, and applying the appropriate serializer.
     * </p>
     *
     * @param securityContextProvider The security context provider for role checks
     * @return The configured SensitiveAnnotationIntrospector
     */
    @Bean
    @NonNull
    public SensitiveAnnotationIntrospector sensitiveAnnotationIntrospector(
            @NonNull SecurityContextProvider securityContextProvider) {
        SensitiveAnnotationIntrospector introspector = new SensitiveAnnotationIntrospector();
        introspector.setSecurityContextProvider(securityContextProvider); // Dependency Injection
        return introspector;
    }

    /**
     * Creates a Jackson module for sensitive data masking.
     * <p>
     * This module is registered with the ObjectMapper to enable the masking functionality.
     * </p>
     *
     * @param sensitiveAnnotationIntrospector The custom annotation introspector
     * @return A configured Jackson module
     */
    @Bean
    @NonNull
    public Module sensitiveDataMaskingModule(@NonNull SensitiveAnnotationIntrospector sensitiveAnnotationIntrospector) {
        SimpleModule module = new SimpleModule("SensitiveDataMaskingModule");
        return module;
    }

    /**
     * Defines a primary {@link ObjectMapper} bean for the application.
     * <p>
     * This ObjectMapper is configured with all discovered Jackson modules and
     * the custom {@link SensitiveAnnotationIntrospector} for masking sensitive data.
     * </p>
     * <p>
     * As this bean is marked as {@link Primary}, it will be used by default throughout
     * the Spring application for JSON serialization/deserialization, ensuring that
     * sensitive data is properly masked in all JSON output.
     * </p>
     * <p>
     * Applications can override this bean by defining their own ObjectMapper bean,
     * but they should ensure that the SensitiveAnnotationIntrospector is properly
     * configured if they want to maintain the masking functionality.
     * </p>
     *
     * @param jacksonModules A list of Jackson modules discovered by Spring Boot
     * @param sensitiveAnnotationIntrospector The custom annotation introspector for masking sensitive data
     * @return A customized ObjectMapper instance
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    @NonNull
    public ObjectMapper objectMapper(@NonNull java.util.List<Module> jacksonModules, 
                                    @NonNull SensitiveAnnotationIntrospector sensitiveAnnotationIntrospector) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(jacksonModules);
        mapper.setAnnotationIntrospector(sensitiveAnnotationIntrospector);
        // Additional configurations here...
        return mapper;
    }
}
