package com.qmh.dao;

import com.qmh.entity.Stock;

public interface StockDAO {
    //根据商品id查询库存信息
    Stock checkStock(Integer id);

    //根据商品id扣除库存
    int updateSale(Stock stock);
}
