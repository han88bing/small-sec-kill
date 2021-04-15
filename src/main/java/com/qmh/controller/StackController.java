package com.qmh.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.qmh.service.OrderService;
import com.qmh.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/stock")
@Slf4j
public class StackController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    //创建令牌桶示例
    private RateLimiter rateLimiter = RateLimiter.create(30); //每秒产生40个token


    @RequestMapping("/md5")
    public String getMd5(Integer id,Integer userId){
        String md5;
        try {
            md5 = orderService.getMd5(id,userId);
        } catch (Exception e) {
            e.printStackTrace();
            return "获取md5失败"+e.getMessage();
        }
        return "获取md5信息为"+md5;
    }

    /**
     * 乐观锁防止超卖+令牌桶算法限流+md5签名+单用户频率访问限制
     * @param id
     * @param userId
     * @param md5
     * @return
     */
    @GetMapping("/kill")
    public String seckill(Integer id,Integer userId,String md5){
        //令牌桶限流
        if(!rateLimiter.tryAcquire(3,TimeUnit.SECONDS)){
            log.info("抢购失败，当前秒杀活动过于火爆，请重试");
            return "抢购失败，当前秒杀活动过于火爆，请重试";
        }
        try{
            //单用户调用接口频率限制
            int count = userService.saveUserCount(userId);
            log.info("用户目前的访问次数为：[{}]",count);
            boolean isBanned = userService.getUserCount(userId);
            if(isBanned){
                log.info("购买失败，超过频率限制");
                return "购买失败，超过频率限制";
            }
            //调用秒杀业务
            int orderId = orderService.seckill(id,userId,md5);
            log.info("秒杀成功，订单id为：" + String.valueOf(orderId));
            return "秒杀成功，订单id为：" + String.valueOf(orderId);
        }catch (Exception e){
           // e.printStackTrace();
            return e.getMessage();
        }
    }


}
