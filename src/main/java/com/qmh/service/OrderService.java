package com.qmh.service;

public interface OrderService {
    /**
     * 下单，返回订单id
     * @param id
     * @return
     */
    int seckill(Integer id,Integer userId,String md5);

    /**
     * 获取md5
     * @param id
     * @param userId
     * @return
     */
    String getMd5(Integer id, Integer userId);
}
