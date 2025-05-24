package me.clickism.configured.localization;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameters {
    String[] value() default {};
}
