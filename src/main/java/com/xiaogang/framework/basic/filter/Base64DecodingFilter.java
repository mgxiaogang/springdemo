package com.xiaogang.framework.basic.filter;

import com.xiaogang.framework.basic.processors.codec.Base64HttpServletRequestWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 20:09
 * @Description:
 */
public class Base64DecodingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Base64HttpServletRequestWrapper decodingRequest = new Base64HttpServletRequestWrapper(request);
        filterChain.doFilter(decodingRequest, response);
    }

}
