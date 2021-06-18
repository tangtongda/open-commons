package com.tangtongda.open.commons.anotations;

import java.lang.annotation.*;

/**
 * {@link ExcelColumn} excel column for bean prop
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2020/12/9
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelColumn {

  /**
   * excel header name unique
   *
   * @return value
   */
  String value() default "";

  /**
   * excel row index
   *
   * @return col
   */
  int col() default 0;
}
