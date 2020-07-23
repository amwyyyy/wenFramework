package com.wen.project.domain;

import com.wen.framework.domain.BaseEntity;
import com.wen.framework.orm.annotation.Column;
import com.wen.framework.orm.annotation.Entity;
import java.io.Serializable;

@Entity(tablename="user")
public class User extends BaseEntity implements Serializable {
    @Column(name="name", jdbcType="VARCHAR", length=32)
    private String name;

    @Column(name="age", jdbcType="INTEGER")
    private Integer age;

    private static final long serialVersionUID = -1811792186L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}