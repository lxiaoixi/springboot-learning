package com.example.mybatis;

import com.example.mybatis.entity.UserEntity;
import com.example.mybatis.enums.UserSexEnum;
import com.example.mybatis.service.impl.RedisServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisApplicationTests {
    @Autowired
    private RedisServiceImpl redisService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void contextLoads() throws InterruptedException {
        redisService.set("neo","xiaoxi");
        Assert.assertNotNull(redisService.get("neo"));
        System.out.println(redisService.get("neo"));
        UserEntity user = new UserEntity();
        user.setUserName("xiaoxi");
        user.setPassWord("123456");
        user.setNickName("xing");
        user.setUserSex(UserSexEnum.MAN);
        user.setAge(10);

        redisService.set("user",user,100L,TimeUnit.MILLISECONDS);
        Thread.sleep(1000);
        System.out.println(redisService.exists("user"));
        redisService.hashSet("users","name","xiaoxi");
        redisService.leftPush("list1","j");
        redisService.leftPush("list1","a");
        redisService.leftPush("list1","v");
        redisService.leftPush("list1","a");
    }

    @Test
    public void testRedisSet(){
        String key1="setMore1";
        String key2="setMore2";
        redisService.setAdd(key1,"it");
        redisService.setAdd(key1,"you");
        redisService.setAdd(key1,"you");
        redisService.setAdd(key1,"know");
        redisService.setAdd(key2,"xx");
        redisService.setAdd(key2,"know");

        System.out.println(redisService.getSetMembers(key1).size());

        Set<Object> diffs = redisService.difference(key1,key2);
        for (Object v:diffs){
            System.out.println("diffs set value :"+v);
        }
    }

    @Test
    public void testRedisZSet(){
        String key="zset";
        redisService.zAdd(key,"it",1);
        redisService.zAdd(key,"you",6);
        redisService.zAdd(key,"know",4);
        redisService.zAdd(key,"neo",3);
        Set<Object> zsets = redisService.rangeByScore(key,0,3);
        for (Object v:zsets){
            System.out.println("zsets value :"+v);
        }
    }

    @Test
    public void ceshi(){
        int a = 5;
        int b = 3;
        int c = 8;
        List<Integer> list = new ArrayList<>();
        List<Integer> places = Arrays.asList(a, b, c);
        list.add(a);
        list.add(b);
        list.add(c);
//        System.out.println(list);
//        Collections.sort(list);
//        System.out.println(list);
        int max = Collections.max(places);
        System.out.println(max);

    }

    @Test
    public void ceshi1(){
        Long value = 291525l;
        Double money = (double)  value/1000;
        System.out.println(money);

        BigDecimal bg  = new BigDecimal(money);
        Double orderPlaceCost = bg.setScale(2, RoundingMode.HALF_UP).doubleValue();
        System.out.println(orderPlaceCost);

        //double   f   =   291.525;

        String f   =   String.valueOf(money);
        BigDecimal   b   =   new   BigDecimal(f);
        double   f1   =   b.setScale(2,   RoundingMode.HALF_UP).doubleValue();
        System.out.println(f1);
    }



}
