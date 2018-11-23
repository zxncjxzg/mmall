package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by 11790 on 2018/10/31.
 */
public class Constant {
    public static final String CURRENT_USER="currentUser";

    public static final String USERNAME="username";
    public static final String EMAIL="email";

    public interface ProductOrderBy{
        Set<String> PRICE_ASC_DESC= Sets.newHashSet("price_desc","price_asc");
    }

    public interface Role{
        int ROLE_CUSTOMER=0;//普通用戶
        int ROLE_ADMIN=1;//管理员
    }

    public enum ProductStatusEnum{
        ON_SALE(1,"在售");
        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.code=code;
            this.value=value;
        }

        public String getValue() {
            return value;
        }
        public int getCode() {
            return code;
        }
    }
}
