package me.clickism.configured.localization;

import java.lang.annotation.*;

/**
 * Annotation to add parameters to a {@link LocalizationKey}.
 * <p>Can only be applied to static fields.</p>
 *
 * <p>Example:</p>
 * <blockquote><pre>
 * enum Keys implements LocalizationKey {
 *    {@literal @}Parameters({"user", "attempts"})
 *     WARN_LOGIN_ATTEMPTS,
 *    {@literal @}Parameters({"username"})
 *     USER_NOT_FOUND,
 * }</pre></blockquote>
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameters {
    /**
     * The parameters associated with the localization key.
     *
     * <p>These parameters can be used in the localization message to replace placeholders.</p>
     * <p>See: {@link LocalizationKey}</p>
     *
     * @return an array of parameter names
     */
    String[] value() default {};
}
