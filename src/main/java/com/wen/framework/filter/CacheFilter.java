package com.wen.framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import redis.clients.jedis.Jedis;

public class CacheFilter implements Filter {
	
    @Override
    public void init(FilterConfig config) throws ServletException {
    	
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        
        String request = req.getRequestURI();
        
        String response = getResponseFromCache(request);
        if (null == response) {
            // 截取生成的html并放入缓存
            ResponseWrapper wrapper = new ResponseWrapper(resp);

            filterChain.doFilter(servletRequest, wrapper);
            
            // 放入缓存
            response = new String(wrapper.getData());
            putIntoCache(request, response);
        }

        // 返回响应
        resp.setContentType("text/html; charset=utf-8");
        resp.getWriter().print(response);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    @Override
    public void destroy() {

    }

    private String getResponseFromCache(String key) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String value = jedis.get(key);
        jedis.close();
        
        return value;
    }

    private void putIntoCache(String key, String html) {
    	Jedis jedis = new Jedis("127.0.0.1", 6379);
    	jedis.set(key, html);
    	jedis.close();
    }
}