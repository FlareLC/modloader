package dev.flarelc.api;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ModuleInfo {
    String name();
    String version();
    String author();
    String description();
    String[] dependencies() default {};
}
