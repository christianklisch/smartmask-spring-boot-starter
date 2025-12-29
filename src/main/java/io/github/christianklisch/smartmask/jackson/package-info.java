/**
 * Package containing Jackson integration classes for the SmartMask library.
 * <p>
 * This package provides the classes necessary to integrate the SmartMask library
 * with Jackson, the JSON serialization library used by Spring Boot. The integration
 * allows fields annotated with {@link io.github.christianklisch.smartmask.annotations.Sensitive}
 * to be automatically masked during JSON serialization.
 * </p>
 * The key classes in this package are:
 * <ul>
 *   <li>{@link io.github.christianklisch.smartmask.jackson.SensitiveAnnotationIntrospector} - 
 *       A Jackson AnnotationIntrospector that detects the {@link io.github.christianklisch.smartmask.annotations.Sensitive}
 *       annotation and applies the appropriate serializer</li>
 *   <li>{@link io.github.christianklisch.smartmask.jackson.SensitiveDataSerializer} - 
 *       A Jackson JsonSerializer that applies masking to sensitive data based on the
 *       configuration in the {@link io.github.christianklisch.smartmask.annotations.Sensitive} annotation</li>
 * </ul>
 * <p>
 * These classes are automatically configured by the {@link io.github.christianklisch.smartmask.autoconfig.SensitiveDataMaskingAutoConfiguration}
 * class and do not need to be manually configured in most cases.
 * </p>
 * 
 * @see io.github.christianklisch.smartmask.jackson.SensitiveAnnotationIntrospector
 * @see io.github.christianklisch.smartmask.jackson.SensitiveDataSerializer
 * @see io.github.christianklisch.smartmask.annotations.Sensitive
 * @see io.github.christianklisch.smartmask.autoconfig.SensitiveDataMaskingAutoConfiguration
 */
@org.springframework.lang.NonNullApi
@org.springframework.lang.NonNullFields
package io.github.christianklisch.smartmask.jackson;