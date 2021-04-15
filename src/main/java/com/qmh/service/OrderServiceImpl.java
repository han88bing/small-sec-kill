package com.qmh.service;

import com.qmh.dao.OrderDAO;
import com.qmh.dao.StockDAO;
import com.qmh.dao.UserDAO;
import com.qmh.entity.Order;
import com.qmh.entity.Stock;
import com.qmh.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService{

    @Autowired
    private StockDAO stockDAO;

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init(){
        redisTemplate.opsForValue().set("kill1","1",20, TimeUnit.SECONDS);
    }


    @Override
    public int seckill(Integer id, Integer userId, String md5) {
       //校验redis中秒杀商品是否超时
        if(!redisTemplate.hasKey("kill"+id)){
            log.info("该商品的秒杀活动已经结束了");
            throw new RuntimeException("该商品的秒杀活动已经结束了");
        }

        //验证签名
        String hashKey = "KEY_"+userId+"_"+id;
        String s = redisTemplate.opsForValue().get(hashKey);
        if(s==null) throw new RuntimeException("没有携带签名");
        if(!md5.equals(s)){
            throw new RuntimeException("当前请求数据不合法");
        }

        Stock stock = checkStock(id);
        updateSale(stock);
        return createOrder(stock);
    }

    @Override
    public String getMd5(Integer id, Integer userId) {
        //验证userId
        User user = userDAO.findById(userId);
        if(user==null) throw new RuntimeException("用户信息不存在");
        log.info("用户信息:[{}]",user.toString());

        //验证id
        Stock stock = stockDAO.checkStock(id);
        if(stock==null) throw  new RuntimeException("商品信息不合法");
        log.info("商品信息：[{}]",stock.toString());

        //生成hashKey
        String hashKey = "KEY_"+userId+"_"+id;
        //生成md5, 其中!jskf是盐
        String key = DigestUtils.md5DigestAsHex((userId+id+"!jskf").getBytes());
        //放入redis中
        redisTemplate.opsForValue().set(hashKey,key,120,TimeUnit.SECONDS);
        return key;
    }

    //校验库存
    private Stock checkStock(Integer id){
        Stock stock = stockDAO.checkStock(id);
        if(stock.getSale().equals(stock.getCount())) {
            throw new RuntimeException("库存不足");
        }
        return stock;
    }

    //扣除库存
    private void updateSale(Stock stock){
        //stock.setSale(stock.getSale()+1);
        //在sql层面完成销量的+1，和版本号的+1，并根据商品id和版本号同时查询更新的商品。
        int result = stockDAO.updateSale(stock);
        if(result==0){
            throw new RuntimeException("抢购失败，请重试");//必须要抛异常，事务可以回滚，否则继续执行下去
        }
    }

    //创建订单
    private Integer createOrder(Stock stock){
        Order order = new Order();
        order.setSid(stock.getId()).setName(stock.getName()).setCreateDate(new Date());
        orderDAO.createOrder(order);
        return order.getId();
    }

}
