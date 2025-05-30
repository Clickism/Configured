/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.localization;

import java.lang.annotation.*;

/**
 * Annotation to add parameters to an enum constant that implements {@link LocalizationKey}.
 * <p>
 * <strong>WARNING: </strong> This annotation can only be applied to enum constants!
 * Using this on another static field, i.E. with {@link LocalizationKey#of(String)}
 * will throw an IllegalArgumentException.
 * <p>
 * Example:
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
     * <p>See: {@link Localization#get(LocalizationKey, Object...)}</p>
     *
     * @return an array of parameter names
     */
    String[] value() default {};
}
