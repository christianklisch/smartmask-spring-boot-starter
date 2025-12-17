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
     * Customizes the ObjectMapper to use our annotation introspector
     * 
     * @param objectMapper The default ObjectMapper
     * @param sensitiveAnnotationIntrospector Our custom annotation introspector
     * @return The customized ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapperCustomizer(ObjectMapper objectMapper, 
                                              SensitiveAnnotationIntrospector sensitiveAnnotationIntrospector) {
        objectMapper.setAnnotationIntrospector(sensitiveAnnotationIntrospector);
        return objectMapper;
    }

    /**
     * Defines a primary ObjectMapper bean and registers the Sensitive Data Masking module.
     * This ObjectMapper will be used by default throughout the Spring application unless another ObjectMapper bean is present.
     * The method ensures that the sensitiveDataMaskingModule—which integrates custom masking logic for handling sensitive data during JSON serialization—is registered with the ObjectMapper.
     * Additional configuration can be added within this method to further customize JSON serialization/deserialization behavior as needed.
     *
     * @param sensitiveDataMaskingModule the Jackson Module that handles sensitive data masking via annotation introspection
     * @return a customized ObjectMapper instance with the masking module registered
     */
     @Bean
     @Primary
     @ConditionalOnMissingBean
     public ObjectMapper objectMapper(Module sensitiveDataMaskingModule) {
         ObjectMapper mapper = new ObjectMapper();
         mapper.registerModule(sensitiveDataMaskingModule);
         // Additional configurations here...
         return mapper;
     }
}
