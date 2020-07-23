package com.wen.project.service;

import com.wen.framework.dao.IBaseDao;
import com.wen.framework.service.BaseService;
import com.wen.project.dao.IOrderDao;
import com.wen.project.dto.OrderDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService extends BaseService<OrderDto, Integer> {
    @Autowired
    private IOrderDao orderDao;

    @Override
    protected IBaseDao<OrderDto, Integer> getDao() {
        return orderDao;
    }
}