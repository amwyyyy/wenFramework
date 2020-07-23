package com.wen.framework.common;

import java.io.Serializable;

/**
 * 分页、排序信息
 * @author huangwg
 *
 */
public class PageParams implements Serializable {
	private static final long serialVersionUID = -2960398574777651921L;
	
	/** 当前页 */
	private int pageNum = 1;

	/** 每页记录数 */
	private int pageSize = 10;

	/** 总记录数 */
	private long total = 0;

	/** 总页数 */
	private int pages = 0;
	
	/** 是否启用分页 */
	private boolean enabled = false;
	
	private String sortName = "";	//排序字段
	
	private String sortType = "";	//排序方式，默认asc
	
	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
