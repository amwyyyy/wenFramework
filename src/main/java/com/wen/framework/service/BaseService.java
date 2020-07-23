package com.wen.framework.service;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.github.orderbyhelper.OrderByHelper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wen.framework.dao.IBaseDao;
import com.wen.framework.domain.BaseEntity;

public abstract class BaseService<E extends BaseEntity, Id extends Serializable> {

	protected abstract IBaseDao<E, Id> getDao();

	public int insert(E entity) {
		return getDao().insert(entity);
	}

	public int updateById(E entity) {
		return getDao().updateById(entity);
	}

	public int deleteById(Id id) {
		return getDao().deleteById(id);
	}

	public E findById(Id id) {
		return getDao().findById(id);
	}
	
	public List<E> findByIds(Id[] ids) {
		return getDao().findByIds(ids);
	}
	
	public E findByEntityId(E entity) {
		if(entity==null){
			return null;
		}
		
		return getDao().findByEntityId(entity);
	}
	
	public List<E> findAll() {
		return getDao().findAll();
	}
	
	public Page<E> findAll(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		return (Page<E>) getDao().findAll();
	}
	
	public List<E> findByCondition(E condition) {
		if (condition == null) {
			return getDao().findAll();
		}
		return getDao().findByCondition(condition);
	}
	
	public List<E> findByCondition(E condition, String sortName, String sortType) {
		sortName = condition.getColumnName(sortName);
		if(StringUtils.isNotEmpty(sortName)) {
			OrderByHelper.orderBy(sortName + " " + StringUtils.defaultString(sortType, "asc"));
		}
		
		return findByCondition(condition);
	}

	public Page<E> findByCondition(E condition, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		if (condition == null) {
			return (Page<E>) this.findAll();
		}
		return (Page<E>) this.findByCondition(condition);
	}
	
	public Page<E> findByCondition(E condition, int pageNum, int pageSize, String sortName, String sortType) {
		if (condition == null) {
			return (Page<E>) this.findAll();
		}
		
		sortName = condition.getColumnName(sortName);
		if(StringUtils.isNotEmpty(sortName)) {
			OrderByHelper.orderBy(sortName + " " + StringUtils.defaultString(sortType, "asc"));
		}
		
		PageHelper.startPage(pageNum, pageSize);
		return (Page<E>) this.findByCondition(condition);
	}
	
	public Integer countAll() {
		return getDao().countAll();
	}

	public Integer countByCondition(E condition) {
		return getDao().countByCondition(condition);
	}
	
//	public void setTimeAndUser(E entity, String user) {
//		if(entity.getId() == null) {
//			entity.setCreateTime(new Date());
//			entity.setCreateUser(user);
//		} else {
//			entity.setUpdateTime(new Date());
//			entity.setUpdateUser(user);
//		}
//	}
}
