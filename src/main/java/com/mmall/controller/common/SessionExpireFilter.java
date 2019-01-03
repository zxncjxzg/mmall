package com.mmall.controller.common;

import com.mmall.common.Constant;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by 11790 on 2019/1/3.
 */
public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest)servletRequest;
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);//从Cookie中获取mmall_login_token的值，也就是sessionId的值
        if(StringUtils.isNotEmpty(loginToken)){
            String userJsonStr= RedisPoolUtil.get(loginToken);
            User user= JsonUtil.stringToObj(userJsonStr,User.class);
            if(user!=null){
                RedisPoolUtil.expire(loginToken, Constant.RedisCacheExtime.REDIS_SESSION_EXTIME);//重新设置Session过期时间
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
