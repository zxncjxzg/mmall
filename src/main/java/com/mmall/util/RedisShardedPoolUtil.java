package com.mmall.util;

import com.mmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

/**
 * Created by 11790 on 2018/12/12.
 * 完全可复用
 */
@Slf4j
public class RedisShardedPoolUtil {

    //重新设置key的有效期：返回1表示设置成功，返回0表示设置未成功
    public static Long expire(String key,int exTime){
        ShardedJedis jedis=null;
        Long result=null;
        try {
            jedis= RedisShardedPool.getJedis();
            result=jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire key:{} error",key,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    /**
     * 用于用户登录过期处理
     * @param key
     * @param value
     * @param exTime 单位为秒
     * @return
     */
    public static String setEx(String key,String value,int exTime){
        ShardedJedis jedis=null;
        String result=null;
        try {
            jedis= RedisShardedPool.getJedis();
            result=jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setEx key:{} value:{} error",key,value,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String set(String key,String value){
        ShardedJedis jedis=null;
        String result=null;
        try {
            jedis= RedisShardedPool.getJedis();
            result=jedis.set(key,value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error",key,value,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String get(String key){
        ShardedJedis jedis=null;
        String result=null;
        try {
            jedis= RedisShardedPool.getJedis();
            result=jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{}  error",key,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key){
        ShardedJedis jedis=null;
        Long result=null;
        try {
            jedis= RedisShardedPool.getJedis();
            result=jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{}  error",key,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

}
