package com.tr.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        for (String exPath : exPaths) {
            if (request.getRequestURI().equals(exPath) || (exPath.endsWith("/**") && request.getRequestURI().startsWith(exPath.replace("/**", "")))) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        Authentication auth = defaultTokenServices.loadAuthentication(token);
//        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

}
