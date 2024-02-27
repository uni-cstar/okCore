package unics.okcore.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Lucio on 2021/7/27.
 * 兼容日志
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface CompatNote {
    String message() default "";
}
