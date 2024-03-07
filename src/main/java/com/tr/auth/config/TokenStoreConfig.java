package com.tr.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @Author TR
 */
@Configuration
public class TokenStoreConfig {

    /**
     * JWT 秘钥
     */
    public static final String SIGN_KEY = "AuthSignKey";

    /**
     * 使用 JwtTokenStore 生成 JWT 令牌
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * TokenEnhancer 的子类，在 JWT 编码的令牌值和 OAuth 身份认证信息之间进行转换
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(SIGN_KEY); // SigningKey 不是用来加密的，jwt 里不要放敏感信息，SigningKey 只是用来验签
        return jwtAccessTokenConverter;
    }

}
