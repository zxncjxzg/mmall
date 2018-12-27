package com.mmall.util;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by 11790 on 2018/12/9.
 * 完全可复用
 */
public class RedisPool {
    private static JedisPool pool;//Jedis连接池

    //连接池配置
    private static Integer maxTotal= Integer.valueOf(PropertiesUtil.getProperty("redis.max.total","20"));//最大连接数
    private static Integer maxIdle=Integer.valueOf(PropertiesUtil.getProperty("redis.max.idle","10"));//在jedispool中的最大的idle状态（空闲的）的jedis实例个数
    private static Integer minIdle=Integer.valueOf(PropertiesUtil.getProperty("redis.min.idle","2"));//在jedispool中的最小的idle状态（空闲的）的jedis实例个数
    private static Boolean testOnBorrow=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));//在borrow一个jedis实例的时候，是否需要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的
    private static Boolean testOnReturn=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true"));//在return一个jedis实例的时候，是否需要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的

    private static String redisIp=PropertiesUtil.getProperty("redis.ip");
    private static Integer redisPort=Integer.parseInt(PropertiesUtil.getProperty("redis.port"));
    //初始化连接池配置，并且创建连接池
    private static void initPool(){
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);
        jedisPoolConfig.setBlockWhenExhausted(true);//连接耗尽时，是否阻塞，false会抛出异常，true阻塞直到超时，默认为true

        pool=new JedisPool(jedisPoolConfig,redisIp,redisPort,1000*2);
    }

    static {
        initPool();
    }

    //从连接池中拿一个实例
    public static Jedis getJedis(){
        return pool.getResource();
    }

    //归还一个jedis实例
    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis=pool.getResource();
        jedis.set("zhou","zhou");
        returnResource(jedis);
        pool.destroy();
        System.out.printf("end");
    }
}
