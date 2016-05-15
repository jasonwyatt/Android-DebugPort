package jwf.debugport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String GROUP_METHOD_INSPECTION = "Method Inspection";
    String GROUP_FIELD_INSPECTION = "Field Inspection";
    String GROUP_ACCESS = "Access";
    String GROUP_OTHER = "Other";

    String group() default GROUP_OTHER;

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Help {
        String value() default "";
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @interface ParamName {
        String value() default "";
    }
}
