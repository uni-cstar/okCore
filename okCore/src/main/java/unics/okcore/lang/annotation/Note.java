package unics.okcore.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by luochao on 2024/2/27.
 * 备注
 */
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Note {
    String message() default "";
}
