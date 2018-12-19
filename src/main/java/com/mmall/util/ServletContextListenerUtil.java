package com.mmall.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by 11790 on 2018/12/18.
 * 完全可复用
 * Servlet的监听器Listener，它是实现了javax.servlet.ServletContextListener接口的服务器端程序，它也是随web应用的启动而启动，
 * 只初始化一次，随web应用的停止而销毁。主要作用是：做一些初始化的内容添加工作、设置一些基本的内容、比如一些参数或者是一些固定的对象等等。
 */
@Slf4j
public class ServletContextListenerUtil implements ServletContextListener {
    /**
     * 监听器的初始化
     * @param servletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("监听器ServletContextListenerUtil初始化");
    }

    /**
     * 监听器销毁
     * @param servletContextEvent
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.info("监听器ServletContextListenerUtil销毁");
    }
}
