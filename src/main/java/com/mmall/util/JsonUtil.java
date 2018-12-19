package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by 11790 on 2018/12/13.
 * 完全可复用
 */
@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper=new ObjectMapper();

    static{
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);

        //在Json序列化的时候，会自动将Date转化为Timestamps：取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
        //所有日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //忽略空Bean转json的错误；比如新建一个对象，但是未为该对象声明任何的属性，该设置默认为true时，程序会报错，如果设为false，则返回{ }
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        //忽略在json字符串中存在，但是在Java对象中不存在的对应属性的情况，防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    /**
     * 将对象转换为字符串
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String objToString(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj:objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }

    /**
     * 将对象【包括对象List】转化为格式化好的字符串
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String objToStringPretty(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj:objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to Pretty String error",e);
            return null;
        }
    }

    /**
     * 将字符串转化为实体对象
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T stringToObj(String str,Class<T> clazz){
        if(StringUtils.isEmpty(str) || clazz == null){
            return null;
        }
        try {
            return clazz.equals(String.class)? (T)str:objectMapper.readValue(str,clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    /**
     * JsonUtil.string2Obj(userListStr, new TypeReference<List<User>>() {
     });
     * @param str
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T stringToObj(String str, TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str) || typeReference==null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)? str:objectMapper.readValue(str,typeReference));
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    /**
     * JsonUtil.string2Obj(userListStr,List.class,User.class);
     * @param str
     * @param collectionClass
     * @param elementClasses
     * @param <T>
     * @return
     */
    public static <T> T stringToObj(String str,Class<?> collectionClass,Class<?>... elementClasses){
        JavaType javaType=objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }


}
