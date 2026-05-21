package com.campus.lostfound.interceptor;

import com.campus.lostfound.common.Constants;
import com.campus.lostfound.exception.UnauthorizedException;
import com.campus.lostfound.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 认证拦截器，校验请求中的JWT Token并注入用户信息到请求属性
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 前置拦截，校验Token有效性
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @return true放行/false拦截
     * @throws UnauthorizedException Token无效时抛出
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader(Constants.TOKEN_HEADER);
        if (!StringUtils.hasText(token) || !token.startsWith(Constants.TOKEN_PREFIX)) {
            throw new UnauthorizedException("未登录或Token无效，请重新登录");
        }

        token = token.substring(Constants.TOKEN_PREFIX.length());
        if (jwtUtil.isTokenExpired(token)) {
            throw new UnauthorizedException("Token已过期，请重新登录");
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            Integer role = jwtUtil.getRoleFromToken(token);
            request.setAttribute("currentUserId", userId);
            request.setAttribute("currentUsername", username);
            request.setAttribute("currentUserRole", role);
            return true;
        } catch (Exception e) {
            log.error("Token解析失败: {}", e.getMessage());
            throw new UnauthorizedException("Token解析失败，请重新登录");
        }
    }
}
