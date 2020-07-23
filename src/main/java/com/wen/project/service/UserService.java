package com.wen.project.service;

import com.wen.framework.dao.IBaseDao;
import com.wen.framework.service.BaseService;
import com.wen.project.dao.IUserDao;
import com.wen.project.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<UserDto, Integer> {
    @Autowired
    private IUserDao userDao;

    @Override
    protected IBaseDao<UserDto, Integer> getDao() {
        return userDao;
    }
}