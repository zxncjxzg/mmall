package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 11790 on 2018/12/20.
 */
@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN=".happymmall.com";
    private final static String COOKIE_NAME="mmall_login_token";//需要传送至客户端浏览器上的

    public static String readLoginToken(HttpServletRequest httpServletRequest){
        Cookie[] cookies=httpServletRequest.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                log.info("read cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                if(StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                    log.info("return cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    public static void writeLoginToken(HttpServletResponse httpServletResponse,String token){
        Cookie cookie=new Cookie(COOKIE_NAME,token);

        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");//设置Cookie生效所对应的请求地址范围
        cookie.setHttpOnly(true);//防止浏览器通过脚本获取到Cookie信息，提高安全性
        //如果不设置maxAge的话，Cookie就不会写入磁盘中，而是写入内存中，随着浏览器的关闭而失效
        cookie.setMaxAge(60 * 60 * 24 * 365);//如果是-1，代表永久；单位是秒

        log.info("write cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
        httpServletResponse.addCookie(cookie);
    }

    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cks = request.getCookies();
        if(cks != null){
            for(Cookie ck : cks){
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0);//设置成0，代表删除此cookie。
                    log.info("del cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }
}
