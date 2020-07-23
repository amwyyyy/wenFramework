package com.wen.framework.orm.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ METHOD, FIELD })
@Retention(RUNTIME)
/**
 * 需要级联时用此注解
 * @author huangwg
 *
 */
public @interface Association {
	/**
	 * (Optional) The name of the column. Defaults to the property or field
	 * name.
	 */
	String columnName() default "";

	/**
	 * (Optional) The name of the column. Defaults to the property or field
	 * name.
	 */
	String jdbcType() default "";
	
	String select();

	String remark() default "";
	
	boolean lazy() default false;
}
