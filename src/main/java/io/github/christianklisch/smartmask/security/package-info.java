/**
 * Package containing security-related classes for the SmartMask library.
 * <p>
 * This package provides classes that integrate the SmartMask library with Spring Security,
 * allowing role-based access control for sensitive data. The integration enables fields
 * annotated with {@link io.github.christianklisch.smartmask.annotations.Sensitive} to be
 * masked or unmasked based on the current user's roles.
 * </p>
 * <p>
 * The primary class in this package is {@link io.github.christianklisch.smartmask.security.SecurityContextProvider},
 * which provides access to the current security context for role-based access checks. This class
 * is used by the {@link io.github.christianklisch.smartmask.jackson.SensitiveDataSerializer} to
 * determine if the current user has the necessary roles to see unmasked sensitive data.
 * </p>
 * <p>
 * The SecurityContextProvider is automatically configured by the
 * {@link io.github.christianklisch.smartmask.autoconfig.SensitiveDataMaskingAutoConfiguration}
 * class and does not need to be manually configured in most cases. However, applications can
 * override this bean by defining their own SecurityContextProvider bean, which will be used
 * instead of the default implementation. This allows for custom role-checking logic if needed.
 * </p>
 * <p>
 * Example usage of role-based access control with the {@link io.github.christianklisch.smartmask.annotations.Sensitive} annotation:
 * </p>
 * <pre>
 * public class User {
 *     private String username;
 *     
 *     &#64;Sensitive(rolesAllowed = {"ROLE_ADMIN", "ROLE_SUPPORT"})
 *     private String socialSecurityNumber;
 *     
 *     // getters and setters
 * }
 * </pre>
 * <p>
 * In this example, the socialSecurityNumber field will only be visible to users with the
 * ROLE_ADMIN or ROLE_SUPPORT role. For all other users, the field will be masked.
 * </p>
 * 
 * @see io.github.christianklisch.smartmask.security.SecurityContextProvider
 * @see io.github.christianklisch.smartmask.annotations.Sensitive
 * @see io.github.christianklisch.smartmask.jackson.SensitiveDataSerializer
 * @see io.github.christianklisch.smartmask.autoconfig.SensitiveDataMaskingAutoConfiguration
 */
@org.springframework.lang.NonNullApi
@org.springframework.lang.NonNullFields
package io.github.christianklisch.smartmask.security;