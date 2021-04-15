package com.qmh.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public int saveUserCount(Integer userId) {
        //根据不同用户id生成调用次数的key
        String limitKey = "LIMIT"+"_"+userId;
        //获取调用次数
        String limitNum = redisTemplate.opsForValue().get(limitKey);
        int limit = -1;
        if(limitNum==null){
            redisTemplate.opsForValue().set(limitKey,"0",3600, TimeUnit.SECONDS);
        }else{
            limit = Integer.parseInt(limitNum)+1;
            redisTemplate.opsForValue().set(limitKey,String.valueOf(limit),3600,TimeUnit.SECONDS);
        }
        return limit;
    }

    @Override
    public boolean getUserCount(Integer userId) {
        //根据不同用户id生成调用次数的key
        String limitKey = "LIMIT"+"_"+userId;
        //获取调用次数
        String limitNum = redisTemplate.opsForValue().get(limitKey);
        if(limitNum==null){
            log.error("该用户没有访问，疑似异常");
            return true;
        }
        return Integer.parseInt(limitNum)>10; //一个用户一小时内只能调用10次
    }
}
