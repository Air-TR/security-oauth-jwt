package com.tr.auth.config;

import com.alibaba.fastjson.JSON;
import com.tr.auth.constant.RedisKey;
import com.tr.auth.kit.JwtKit;
import com.tr.auth.kit.ServletKit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author TR
 */
@Component
public class TokenFilter extends OncePerRequestFilter {

    @Value("${security.ex-path}")
    private String securityExPath;

    private static List<String> exPaths = new ArrayList<>();

    @PostConstruct
    public void initSecurityExPath() {
        exPaths = Arrays.stream(securityExPath.split(",")).collect(Collectors.toList());
    }

    @Resource
    private DefaultTokenServices defaultTokenServices;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        for (String exPath : exPaths) {
            if (request.getRequestURI().equals(exPath) || (exPath.endsWith("/**") && request.getRequestURI().startsWith(exPath.replace("/**", "")))) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!token.equals(stringRedisTemplate.opsForValue().get(RedisKey.TOKEN + JwtKit.getUsername(token)))) {
            ServletKit.renderString(response, 401, "无效 token");
            return;
        }
        // 方式一（没有解决赋予 authorities 问题，用不了 @PreAuthorize、@RolesAllowed、@Secured 注解）
//        try {
//            OAuth2Authentication authentication = defaultTokenServices.loadAuthentication(token); // 会验证 token 是否过期，但是上一步 redis 判断已经拦截了过期的 token
//        } catch (AuthenticationException | InvalidTokenException e) {
//            ServletKit.renderString(response, 401, e.getMessage());
//            return;
//        }
        // 方式二（可以赋予 authorities）
        List<SimpleGrantedAuthority> authorities = JSON.parseArray(JwtKit.getAuthorities().toString(), SimpleGrantedAuthority.class);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(token, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

}
