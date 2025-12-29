/**
 * Package containing annotations used by the SmartMask library.
 * <p>
 * This package provides annotations that can be used to mark fields or methods
 * as containing sensitive data that should be masked during serialization or logging.
 * </p>
 * <p>
 * The primary annotation in this package is {@link io.github.christianklisch.smartmask.annotations.Sensitive},
 * which is used to mark fields or getter methods that contain sensitive data. This annotation
 * can be configured with various parameters to control how the data is masked.
 * </p>
 * <p>
 * Example usage:
 * </p>
 * <pre>
 * public class User {
 *     private String username;
 *     
 *     &#64;Sensitive(type = MaskType.EMAIL)
 *     private String email;
 *     
 *     &#64;Sensitive(showFirst = 0, showLast = 4, maskChar = '*')
 *     private String creditCardNumber;
 *     
 *     &#64;Sensitive(rolesAllowed = {"ROLE_ADMIN", "ROLE_SUPPORT"})
 *     private String socialSecurityNumber;
 *     
 *     // getters and setters
 * }
 * </pre>
 * 
 * @see io.github.christianklisch.smartmask.annotations.Sensitive
 * @see io.github.christianklisch.smartmask.MaskType
 */
@org.springframework.lang.NonNullApi
@org.springframework.lang.NonNullFields
package io.github.christianklisch.smartmask.annotations;

import io.github.christianklisch.smartmask.MaskType;