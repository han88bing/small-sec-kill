package com.qmh.dao;

import com.qmh.entity.Order;

public interface OrderDAO {
    /**
     * 生成订单
     * @param order
     */
    void createOrder(Order order);
}
