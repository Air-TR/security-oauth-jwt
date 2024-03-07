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
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
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
import javax.sql.DataSource;
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
    @Resource
    private DataSource dataSource;

    /**
     * 客户端详情服务配置
     * @param clients the client details configurer
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        /**
         * 配置方式一（直接代码定义）
         */
        clients.inMemory()
                .withClient("auth")
                .secret(passwordEncoder.encode("123456")) // $2a$10$VVhz0JEm3uNQPxdx3vgdDuYgxe4e6X7SfNlewXfdtchirGUgfybTS —— 123456 对应的密文
                .scopes("read","write") // all
                .accessTokenValiditySeconds(3600)
                .authorizedGrantTypes("password", "refresh_token");
//                .autoApprove(true) //登录后绕过批准询问(/oauth/confirm_access)
//                .refreshTokenValiditySeconds(36000);

        /**
         * 配置方式二（DB 定义 —— oauth_client_details 表）
         *  设置 clients，使用 JdbcClientDetailsService 自动获取 oauth_client_details 表信息
         *  如需自定义，在这里配置自定义 Client 内容，如使用 ClientService 获取 clientList 后通过 builder（用 clients.inMemory() 获取）配置，参考 orion
         */
//        clients.withClientDetails(new JdbcClientDetailsService(dataSource));
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        if (jwtAccessTokenConverter != null) {
            if (tokenEnhancer != null) {
                // token 增强（这里的主要目的是使用 CusTokenEnhancer 给 token 增加自定义信息）
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
//                .tokenGranter(new CompositeTokenGranter(getTokenGranters(endpoints))) // 可以不自定义，不影响 refresh_token 使用
                .accessTokenConverter(jwtAccessTokenConverter);
//                .setClientDetailsService(ClientService)      // 如果上面 ClientDetailsServiceConfigurer 采用自定义配置，这里需要开启配置，并且 ClientService 需要继承 ClientDetailsService 重写 loadClientByClientId(String clientId) 方法，参考 orion
    }

    private List<TokenGranter> getTokenGranters(AuthorizationServerEndpointsConfigurer endpoints) {
        AuthorizationServerTokenServices tokenServices = endpoints.getTokenServices();
        OAuth2RequestFactory requestFactory = endpoints.getOAuth2RequestFactory();
        ClientDetailsService clientDetailsService = endpoints.getClientDetailsService();
        // 密码 Token 授权
        ResourceOwnerPasswordTokenGranter resourceOwnerPasswordTokenGranter = new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices, clientDetailsService, requestFactory);
        // 刷新 Token 授权
        RefreshTokenGranter refreshTokenGranter = new RefreshTokenGranter(tokenServices, clientDetailsService, requestFactory);
        // 授权码 Token 授权
        AuthorizationCodeTokenGranter authorizationCodeTokenGranter = new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices(), clientDetailsService, requestFactory);
        // 客户端凭证 Token 授权
        ClientCredentialsTokenGranter clientCredentialsTokenGranter = new ClientCredentialsTokenGranter(tokenServices, clientDetailsService, requestFactory);
        // 隐式 Token 授权
        ImplicitTokenGranter implicitTokenGranter = new ImplicitTokenGranter(tokenServices, clientDetailsService, requestFactory);
        // 手机号 Token 授权
//        PhoneTokenGranter phoneTokenGranter = new PhoneTokenGranter(userService, tokenServices, appService, requestFactory);
        // 身份证 Token 授权
//        IdCardTokenGranter idCardTokenGranter = new IdCardTokenGranter(userService, tokenServices, clientDetailsService, requestFactory);
        return Lists.newArrayList(resourceOwnerPasswordTokenGranter, refreshTokenGranter, authorizationCodeTokenGranter, clientCredentialsTokenGranter, implicitTokenGranter);
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("permitAll()")   // isAuthenticated()
                .tokenKeyAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

}