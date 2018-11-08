package com.mmall.common;

/**
 * Created by 11790 on 2018/10/31.
 */
public class Constant {
    public static final String CURRENT_USER="currentUser";

    public static final String USERNAME="username";
    public static final String EMAIL="email";

    public interface Role{
        int ROLE_CUSTOMER=0;//普通用戶
        int ROLE_ADMIN=1;//管理员
    }
}
