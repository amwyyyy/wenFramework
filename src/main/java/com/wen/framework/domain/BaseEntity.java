package com.wen.framework.domain;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

import com.wen.framework.orm.annotation.Column;
import com.wen.framework.utils.BeanUtils;

/**
 * Entity基类，所有Entity类应该继承于此类
 * @author amwyyyy
 */
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = 8382925958365138911L;
	
	@Column(name="id", jdbcType="INTEGER", pk=true, remark="ID")
    private Integer id;
	
//	@Column(name="create_time", jdbcType="TIMESTAMP", remark="创建时间")
//	private Date createTime;
//	
//	@Column(name="create_user", jdbcType="VARCHAR", length=64, remark="创建人")
//	private String createUser;
//	
//	@Column(name="update_time", jdbcType="TIMESTAMP", remark="更新时间")
//	private Date updateTime;
//	
//	@Column(name="update_user", jdbcType="VARCHAR", length=64, remark="更新人")
//	private String updateUser;
//	
//	@Column(name="enable", jdbcType="INTEGER", remark="有效标识，0无效，1有效")
//	private Integer enable;
	
	/**
	 * 根据对象字段名取数据表字段名
	 * @param fieldName
	 * @return
	 */
	public String getColumnName(String fieldName) {
		Field field = BeanUtils.findSuperField(this.getClass(), fieldName);
		if(field != null) {
			Column column = field.getAnnotation(Column.class);
			if(column != null) {
				return column.name();
			}
		}
		return "";
	}
	
//	public Date getCreateTime() {
//		return createTime;
//	}
//	public void setCreateTime(Date createTime) {
//		this.createTime = createTime;
//	}
//	public String getCreateUser() {
//		return createUser;
//	}
//	public void setCreateUser(String createUser) {
//		this.createUser = createUser;
//	}
//	public Date getUpdateTime() {
//		return updateTime;
//	}
//	public void setUpdateTime(Date updateTime) {
//		this.updateTime = updateTime;
//	}
//	public String getUpdateUser() {
//		return updateUser;
//	}
//	public void setUpdateUser(String updateUser) {
//		this.updateUser = updateUser;
//	}
//	public Integer getEnable() {
//		return enable;
//	}
//	public void setEnable(Integer enable) {
//		this.enable = enable;
//	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
}
