package com.github.joostvdg.dui.logging;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface KubernetesCompatible {
    public boolean value() default true;
}
