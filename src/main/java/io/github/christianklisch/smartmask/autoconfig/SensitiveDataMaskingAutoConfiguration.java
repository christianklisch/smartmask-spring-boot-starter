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

/**
 * Auto-configuration for the Sensitive Data Masking library.
 * Registers the custom Jackson AnnotationIntrospector.
 */
@AutoConfiguration
@ConditionalOnClass({ObjectMapper.class, Module.class}) // Only activate if Jackson is available
public class SensitiveDataMaskingAutoConfiguration {

    /**
     * Ensures that SecurityContextProvider exists as a Bean
     *
     * @return A new SecurityContextProvider if none exists
     */
    @Bean
    @ConditionalOnMissingBean
    public SecurityContextProvider securityContextProvider() {
        return new SecurityContextProvider();
    }

    /**
     * Creates the SensitiveAnnotationIntrospector bean
     *
     * @param securityContextProvider The security context provider for role checks
     * @return The configured SensitiveAnnotationIntrospector
     */
    @Bean
    public SensitiveAnnotationIntrospector sensitiveAnnotationIntrospector(
            SecurityContextProvider securityContextProvider) {
        SensitiveAnnotationIntrospector introspector = new SensitiveAnnotationIntrospector();
        introspector.setSecurityContextProvider(securityContextProvider); // Dependency Injection
        return introspector;
    }

    /**
     * Creates a Jackson module that registers our custom annotation introspector
     *
     * @param sensitiveAnnotationIntrospector The custom annotation introspector
     * @return A configured Jackson module
     */
    @Bean
    public Module sensitiveDataMaskingModule(SensitiveAnnotationIntrospector sensitiveAnnotationIntrospector) {
        SimpleModule module = new SimpleModule("SensitiveDataMaskingModule");
        return module;
    }

    /**
     * Defines a primary ObjectMapper bean, configures it with all discovered Jackson modules,
     * and sets the custom SensitiveAnnotationIntrospector.
     * This ObjectMapper will be used by default throughout the Spring application for JSON serialization/deserialization.
     * It ensures that any auto-configured Jackson modules are registered and that sensitive data handling
     * is enabled through the custom introspector.
     *
     * @param jacksonModules                 A list of Jackson modules discovered by Spring Boot, to be registered with the ObjectMapper.
     * @param sensitiveAnnotationIntrospector The custom annotation introspector for masking sensitive data.
     * @return A customized ObjectMapper instance.
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper(java.util.List<Module> jacksonModules, SensitiveAnnotationIntrospector sensitiveAnnotationIntrospector) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(jacksonModules);
        mapper.setAnnotationIntrospector(sensitiveAnnotationIntrospector);
        // Additional configurations here...
        return mapper;
    }
}