package com.wen.framework.common;

/**
 * 导出Excel数值转换器
 * @author huangwg
 */
public class ExcelConverter {
	public static String convert(String convertor, Object value) {
		switch(convertor) {
		case "1" :
			return "123";
		case "2" :
			return "234";
		default : return value==null ? "" : value.toString();
		}
	}
}
