package com.common.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author chengwei
 * @date 2018/5/3 16:55
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class RedisHelperTest {
    @Resource
    RedisHelper redisHelper;

    @Test
    public void insertsTest() throws Exception {
        redisHelper.set("test","1");
        System.out.println(redisHelper.get("test", String.class).toString());
    }

}