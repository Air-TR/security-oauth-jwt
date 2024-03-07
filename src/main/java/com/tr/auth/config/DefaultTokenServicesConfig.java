package com.tr.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.Resource;

/**
 * @Author: TR
 */
@Configuration
public class DefaultTokenServicesConfig {

    @Resource
    private TokenStore tokenStore; // TokenStoreConfig 中定义

//    @Resource
//    private AuthenticationManager authenticationManager; // WebSecurityConfig 中定义

//    @Resource
//    private TokenEnhancer tokenEnhancer; // WebSecurityConfig 中定义

    @Bean
    public DefaultTokenServices defaultTokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore);
//        defaultTokenServices.setTokenEnhancer(tokenEnhancer);
//        defaultTokenServices.setAuthenticationManager(authenticationManager);
        // 支持使用 refresh_token 刷新 access_token
        defaultTokenServices.setSupportRefreshToken(true);
        // 允许重复使用 refresh_token
        defaultTokenServices.setReuseRefreshToken(true);
        return defaultTokenServices;
    }

}
