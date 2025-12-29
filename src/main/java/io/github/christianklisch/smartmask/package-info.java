/**
 * Root package for the SmartMask library, a unified data masking solution for Spring Boot applications.
 * <p>
 * SmartMask provides a simple and consistent way to mask sensitive data in both JSON responses
 * and log messages. It uses a single annotation-based approach to define which fields should be
 * masked and how they should be masked.
 * </p>
 * Key features:
 * <ul>
 *   <li>Annotation-based masking with {@link io.github.christianklisch.smartmask.annotations.Sensitive}</li>
 *   <li>Multiple masking strategies for different data types</li>
 *   <li>Role-based access control for unmasked data</li>
 *   <li>Automatic integration with Jackson for JSON serialization</li>
 *   <li>Integration with Logback for log message masking</li>
 * </ul>
 * <p>
 * The library is designed to be easy to use and integrate with existing Spring Boot applications.
 * Simply add the dependency to your project, and the auto-configuration will take care of the rest.
 * </p>
 * 
 * @see io.github.christianklisch.smartmask.annotations.Sensitive
 * @see io.github.christianklisch.smartmask.MaskType
 */
@org.springframework.lang.NonNullApi
@org.springframework.lang.NonNullFields
package io.github.christianklisch.smartmask;