package com.wen.framework.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段信息
 * @author denis.huang
 * @date 2016年4月26日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumn
{
	/** 列宽 */
	int width() default 15;
	
	/** 背景色，默认白色 */
	short color() default 0x9;
	
	/** 对齐方式，默认居中 */
	short alignment() default 0x0;
	
	/** 日期类型格式化 */
	String format() default "yyyy-MM-dd";
	
	/** 字体名 */
	String fontName() default "宋体";
	
	/** 字体大小 */
	short fontSize() default 12;
	
	short fontColor() default 8;
	
	/** 字体加粗，默认不加粗 */
	short bold() default 0x190;
	
	/** 下划线 */
	byte underline() default 0;
}
