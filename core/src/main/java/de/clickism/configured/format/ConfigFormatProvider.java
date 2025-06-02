/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.configured.format;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ServiceLoader;

/**
 * Abstract class for providing configuration formats based on file extensions.
 * This class uses the ServiceLoader mechanism to discover implementations at runtime.
 */
public abstract class ConfigFormatProvider {
    private static final StackWalker STACK_WALKER =
            StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    /**
     * Gets the configuration format for a given file path.
     *
     * @param path   the file path, which must include an extension
     * @param caller the caller class, used to load the service provider
     * @return the corresponding ConfigFormat for the file extension
     * @throws IllegalArgumentException if the path does not have an extension
     *                                  or if no format is found for the extension
     */
    public static @NotNull ConfigFormat getFormat(String path, Class<?> caller) throws IllegalArgumentException {
        String extension = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : null;
        if (extension == null) {
            throw new IllegalArgumentException("Path must have an extension: " + path);
        }
        for (ConfigFormatProvider formatExtension : getFormatProviders(caller)) {
            ConfigFormat format = formatExtension.getFormatFor(extension);
            if (format != null) {
                return format;
            }
        }
        throw new IllegalArgumentException("No format found for extension: " + extension);
    }

    private static Iterable<ConfigFormatProvider> getFormatProviders(Class<?> caller) {
        ClassLoader classLoader = caller.getClassLoader();
        return ServiceLoader.load(ConfigFormatProvider.class, classLoader);
    }

    /**
     * Gets the class of the caller that invoked this method.
     *
     * @return the class of the caller
     */
    public static Class<?> getCallerClass() {
        return STACK_WALKER.walk(frames ->
                frames.skip(2)
                        .findFirst()
                        .map(StackWalker.StackFrame::getDeclaringClass)
                        .orElseThrow(() -> new IllegalStateException("Caller class not found"))
        );
    }

    /**
     * Gets the configuration format for a specific file extension.
     *
     * @param extension the file extension to look up
     * @return the ConfigFormat for the given extension, or null if not found
     */
    public abstract @Nullable ConfigFormat getFormatFor(String extension);
}
