package com.wen.framework.common;

/**
 * 导出excel基础类
 * @author denis.huang
 * @date 2016年4月27日
 */
public abstract class BaseEO<E> {
	public abstract E convert(Object bean);
}
