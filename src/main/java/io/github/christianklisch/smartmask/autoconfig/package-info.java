/**
 * Package containing Spring Boot auto-configuration classes for the SmartMask library.
 * <p>
 * This package provides the auto-configuration necessary to integrate the SmartMask
 * library with Spring Boot applications. The auto-configuration automatically registers
 * the necessary beans and configures Jackson to use the SmartMask serializers.
 * </p>
 * <p>
 * The primary class in this package is {@link io.github.christianklisch.smartmask.autoconfig.SensitiveDataMaskingAutoConfiguration},
 * which is automatically detected by Spring Boot's auto-configuration mechanism when the
 * SmartMask library is added as a dependency to a Spring Boot application.
 * </p>
 * The auto-configuration:
 * <ul>
 *   <li>Creates a {@link io.github.christianklisch.smartmask.security.SecurityContextProvider} bean if none exists</li>
 *   <li>Configures a {@link io.github.christianklisch.smartmask.jackson.SensitiveAnnotationIntrospector} for Jackson</li>
 *   <li>Creates a Jackson module for sensitive data masking</li>
 *   <li>Provides a primary {@link com.fasterxml.jackson.databind.ObjectMapper} configured for sensitive data masking</li>
 * </ul>
 * <p>
 * No manual configuration is required to use the SmartMask library in a Spring Boot application.
 * Simply add the library as a dependency, and the auto-configuration will take care of the rest.
 * </p>
 * 
 * @see io.github.christianklisch.smartmask.autoconfig.SensitiveDataMaskingAutoConfiguration
 * @see io.github.christianklisch.smartmask.annotations.Sensitive
 * @see io.github.christianklisch.smartmask.jackson.SensitiveAnnotationIntrospector
 * @see io.github.christianklisch.smartmask.security.SecurityContextProvider
 */
@org.springframework.lang.NonNullApi
@org.springframework.lang.NonNullFields
package io.github.christianklisch.smartmask.autoconfig;

import com.fasterxml.jackson.databind.ObjectMapper;