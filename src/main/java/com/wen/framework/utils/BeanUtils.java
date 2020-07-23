package com.wen.framework.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BeanUtils {

	/**
	 * 根据字段名取get方法名
	 * @param field
	 * @return
	 */
	public static String getMethodName(String field) {
		if(Character.isUpperCase(field.charAt(0))) {
			return "get" + field;
		} else {
			return (new StringBuilder("get").append(Character.toUpperCase(field.charAt(0)))
					.append(field.substring(1)).toString());
		}
	}
	
	/**
	 * 根据字段名取set方法名
	 * @param field
	 * @return
	 */
	public static String setMethodName(String field) {
		if(Character.isUpperCase(field.charAt(0))) {
			return "set" + field;
		} else {
			return (new StringBuilder("set").append(Character.toUpperCase(field.charAt(0)))
					.append(field.substring(1)).toString());
		}
	} 
	
	/**
	 * 根据字段名取get方法
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static Method findGetMethod(Class<?> clazz, String fieldName) throws NoSuchMethodException, SecurityException {
		return clazz.getDeclaredMethod(getMethodName(fieldName));
	}
	
	/**
	 * 根据字段名取set方法
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static Method findSetMethod(Class<?> clazz, String fieldName) throws NoSuchMethodException, SecurityException {
		return clazz.getDeclaredMethod(setMethodName(fieldName));
	}

	/**
	 * 根据字段名和目标对象取值 
	 */
	public static Object getValue(Class<?> clazz, String fieldName, Object obj) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(obj == null) return null;
		Method method = findGetMethod(clazz, fieldName);
		return method.invoke(obj);
	}
	
	/**
	 * 根据字段名和目标对象取值,包括父类 
	 */
	public static Object getSuperValue(Class<?> clazz, String fieldName, Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(obj == null) return null;
		Method method = findSuperGetMethod(clazz, fieldName);
		return method.invoke(obj);
	}
	
	/**
	 * 递归取类以及父类的所有属性
	 */
	public static Field[] findSuperFields(Class<?> clazz) {
		Map<String, Field> map = new LinkedHashMap<>();
		findSuperFields(map, clazz);
		return map.values().toArray(new Field[map.size()]);
	}
	
	/**
	 * 查找属性，包括父类
	 */
	public static Field findSuperField(Class<?> clazz, String fieldName) {
		Map<String, Field> map = new HashMap<>();
		findSuperFields(map, clazz);
		return map.get(fieldName);
	}

	private static void findSuperFields(Map<String, Field> map, Class<?> clazz) {
		if (clazz == null) {
			return;
		}

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (!map.containsKey(field.getName())) {
				map.put(field.getName(), field);
			}
		}

		findSuperFields(map, clazz.getSuperclass());
	}
	
	/**
	 * 查找get方法，包括父类
	 */
	public static Method findSuperGetMethod(Class<?> clazz, String fieldName) {
		Map<String, Method> map = new HashMap<>();
		findSuperMethods(map, clazz);
		return map.get(getMethodName(fieldName));
	}
	
	private static void findSuperMethods(Map<String, Method> map, Class<?> clazz) {
		if (clazz == null) {
			return;
		}
		
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (!map.containsKey(method.getName())) {
				map.put(method.getName(), method);
			}
		}

		findSuperMethods(map, clazz.getSuperclass());
	}
}
