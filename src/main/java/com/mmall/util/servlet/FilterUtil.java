package com.mmall.util.servlet;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by 11790 on 2018/12/18.
 * 部分可复用
 * Servlet中的过滤器Filter是实现了javax.servlet.Filter接口的服务器端程序，主要的用途是过滤字符编码、做一些业务逻辑判断等。
 * 其工作原理是，只要你在web.xml文件配置好要拦截的客户端请求，它都会帮你拦截到请求，
 * 此时你就可以对请求或响应(Request、Response)统一设置编码，简化操作；同时还可以进行逻辑判断，
 * 如用户是否已经登录、有没有权限访问该页面等等工作，它是随你的web应用启动而启动的，只初始化一次，以后就可以拦截相关的请求，
 * 只有当你的web应用停止或重新部署的时候才能销毁。
 */
@Slf4j
public class FilterUtil implements Filter{

    private FilterConfig filterConfig;

    /**
     * 用于完成过滤器的初始化
     * @param filterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig=filterConfig;
        log.info("过滤器Filter初始化");
    }

    /**
     * 实现过滤功能，该方法对每个请求增加额外的处理
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)){
            throw new ServletException("FilterUtil just supports HTTP requests");
        }
        HttpServletRequest httpServletRequest=(HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse=(HttpServletResponse) servletResponse;
        httpServletRequest.setCharacterEncoding(this.filterConfig.getInitParameter("encoding"));
        httpServletResponse.setCharacterEncoding(this.filterConfig.getInitParameter("encoding"));
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }

    /**
     * 用于过滤器销毁前，完成某些资源的回收
     */
    @Override
    public void destroy() {
        log.info("过滤器Filter销毁");
    }
}
