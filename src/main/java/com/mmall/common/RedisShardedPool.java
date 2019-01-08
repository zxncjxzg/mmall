package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11790 on 2019/1/7.
 */
public class RedisShardedPool {
    private static ShardedJedisPool pool;//ShardedJedis连接池

    //连接池配置
    private static Integer maxTotal= Integer.valueOf(PropertiesUtil.getProperty("redis.max.total","20"));//最大连接数
    private static Integer maxIdle=Integer.valueOf(PropertiesUtil.getProperty("redis.max.idle","10"));//在jedispool中的最大的idle状态（空闲的）的jedis实例个数
    private static Integer minIdle=Integer.valueOf(PropertiesUtil.getProperty("redis.min.idle","2"));//在jedispool中的最小的idle状态（空闲的）的jedis实例个数
    private static Boolean testOnBorrow=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));//在borrow一个jedis实例的时候，是否需要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的
    private static Boolean testOnReturn=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true"));//在return一个jedis实例的时候，是否需要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的

    private static String redis1Ip=PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port=Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    private static String redis2Ip=PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port=Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));
    //初始化连接池配置，并且创建连接池
    private static void initPool(){
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);
        jedisPoolConfig.setBlockWhenExhausted(true);//连接耗尽时，是否阻塞，false会抛出异常，true阻塞直到超时，默认为true

        JedisShardInfo info1=new JedisShardInfo(redis1Ip,redis1Port,1000*2);//超时时间为2秒
        JedisShardInfo info2=new JedisShardInfo(redis2Ip,redis2Port,1000*2);

        List<JedisShardInfo> jedisShardInfoList=new ArrayList<JedisShardInfo>(2);
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        pool=new ShardedJedisPool(jedisPoolConfig,jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
        initPool();
    }

    //从连接池中拿一个实例
    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    //归还一个jedis实例
    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis=pool.getResource();
        for(int i=0;i<10;i++){
            jedis.set("key"+i,"value"+i);
        }
        returnResource(jedis);//归还ShardedJedis连接
        System.out.printf("end");
    }
}
