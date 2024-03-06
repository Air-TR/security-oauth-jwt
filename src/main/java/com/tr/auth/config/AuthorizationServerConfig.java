package com.tr.auth.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: TR
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserDetailsService userDetailsService; // CusUserDetailsService 中重写
    @Resource
    private TokenStore tokenStore; // TokenStoreConfig 中定义
    @Resource
    private JwtAccessTokenConverter jwtAccessTokenConverter; // TokenStoreConfig 中定义
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private TokenEnhancer tokenEnhancer; // WebSecurityConfig 中定义

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("auth")
                .secret(passwordEncoder.encode("123456")) // $2a$10$VVhz0JEm3uNQPxdx3vgdDuYgxe4e6X7SfNlewXfdtchirGUgfybTS —— 123456 对应的密文
                .scopes("read","write") // all
                .accessTokenValiditySeconds(3600)
                .authorizedGrantTypes("password", "refresh_token");
//                .autoApprove(true) //登录后绕过批准询问(/oauth/confirm_access)
//                .refreshTokenValiditySeconds(36000);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        if (jwtAccessTokenConverter != null) {
            if (tokenEnhancer != null) {
                // token 增强（这里的主要目的是使用 MyTokenEnhancer 给 token 增加自定义信息）
                TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
                tokenEnhancerChain.setTokenEnhancers(
                        Arrays.asList(tokenEnhancer, jwtAccessTokenConverter));
                endpoints.tokenEnhancer(tokenEnhancerChain);
            } else {
                endpoints.accessTokenConverter(jwtAccessTokenConverter);
            }
        }
        endpoints
                .authenticationManager(authenticationManager)  // 要支持密码登录授权模式，需配置此项
                .userDetailsService(userDetailsService)        // 要支持密码登录授权模式，需配置此项
                .tokenStore(tokenStore)
                .tokenGranter(new CompositeTokenGranter(getTokenGranters(endpoints)))
                .accessTokenConverter(jwtAccessTokenConverter);
//                .setClientDetailsService(ClientService)      // 如果上面 ClientDetailsServiceConfigurer 采用自定义配置，这里需要开启配置，并且 ClientService 需要继承 ClientDetailsService 重写 loadClientByClientId(String clientId) 方法，参考 orion
    }

    private List<TokenGranter> getTokenGranters(AuthorizationServerEndpointsConfigurer endpoints) {
        AuthorizationServerTokenServices tokenServices = endpoints.getTokenServices();
        OAuth2RequestFactory requestFactory = endpoints.getOAuth2RequestFactory();
        ClientDetailsService clientDetailsService = endpoints.getClientDetailsService();
//        PhoneTokenGranter phoneTokenGranter = new PhoneTokenGranter(userService, tokenServices, appService, requestFactory);
        ClientCredentialsTokenGranter clientCredentialsTokenGranter = new ClientCredentialsTokenGranter(tokenServices, clientDetailsService, requestFactory);
        ResourceOwnerPasswordTokenGranter resourceOwnerPasswordTokenGranter = new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices, clientDetailsService, requestFactory);
//        AuthorizationCodeTokenGranter authorizationCodeTokenGranter = new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices(), clientDetailsService, requestFactory);
        ImplicitTokenGranter implicitTokenGranter = new ImplicitTokenGranter(tokenServices, clientDetailsService, requestFactory);
//        IdCardTokenGranter idCardTokenGranter = new IdCardTokenGranter(userService, tokenServices, clientDetailsService, requestFactory);
        RefreshTokenGranter refreshTokenGranter = new RefreshTokenGranter(tokenServices, clientDetailsService, requestFactory);
//        return Lists.newArrayList(phoneTokenGranter, clientCredentialsTokenGranter, resourceOwnerPasswordTokenGranter, refreshTokenGranter, idCardTokenGranter, implicitTokenGranter, authorizationCodeTokenGranter);
        return Lists.newArrayList(clientCredentialsTokenGranter, resourceOwnerPasswordTokenGranter, refreshTokenGranter, implicitTokenGranter);
    }

//    @Bean
//    public AuthorizationCodeServices authorizationCodeServices() {
//        return new JdbcAuthorizationCodeServices(dataSource);
//    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("permitAll()")   // isAuthenticated()
                .tokenKeyAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

}