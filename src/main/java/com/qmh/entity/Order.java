package com.qmh.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
@Data
@Accessors(chain = true)
public class Order {
    private Integer id;//订单id
    private Integer sid;//商品id
    private String name;
    private Date createDate;
}
