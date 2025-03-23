package ru.extoozy.web.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import ru.extoozy.constant.ApiEndpointConstants;
import ru.extoozy.util.CachedBodyHttpServletRequest;

import java.io.IOException;

@WebFilter(ApiEndpointConstants.ALL_ENDPOINTS)
public class CacheBodyFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest((HttpServletRequest) request);
        chain.doFilter(cachedBodyHttpServletRequest, response);
    }
}
