/**
 * Package containing logging integration classes for the SmartMask library.
 * <p>
 * This package provides classes that integrate the SmartMask library with logging
 * frameworks, allowing sensitive data to be automatically masked in log messages.
 * </p>
 * <p>
 * The primary class in this package is {@link io.github.christianklisch.smartmask.logging.MaskingMessageConverter},
 * which is a Logback converter that inspects log arguments for fields annotated with
 * {@link io.github.christianklisch.smartmask.annotations.Sensitive} and masks them before
 * they appear in the logs.
 * </p>
 * <p>
 * To use the MaskingMessageConverter in your Logback configuration, add it to your logback.xml file:
 * </p>
 * <pre>
 * &lt;configuration&gt;
 *     &lt;conversionRule conversionWord="maskedMsg" 
 *                    converterClass="io.github.christianklisch.smartmask.logging.MaskingMessageConverter" /&gt;
 *     
 *     &lt;appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender"&gt;
 *         &lt;encoder&gt;
 *             &lt;pattern&gt;%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %maskedMsg%n&lt;/pattern&gt;
 *         &lt;/encoder&gt;
 *     &lt;/appender&gt;
 *     
 *     &lt;root level="info"&gt;
 *         &lt;appender-ref ref="CONSOLE" /&gt;
 *     &lt;/root&gt;
 * &lt;/configuration&gt;
 * </pre>
 * <p>
 * This will ensure that any objects logged with SLF4J that contain fields annotated with
 * {@link io.github.christianklisch.smartmask.annotations.Sensitive} will have those fields
 * masked in the log output.
 * </p>
 * 
 * @see io.github.christianklisch.smartmask.logging.MaskingMessageConverter
 * @see io.github.christianklisch.smartmask.annotations.Sensitive
 */
@org.springframework.lang.NonNullApi
@org.springframework.lang.NonNullFields
package io.github.christianklisch.smartmask.logging;