package com.tr.auth.service.impl;

import com.google.common.collect.Maps;
import com.tr.auth.config.CusAuthentication;
import com.tr.auth.config.PasswordEncoderKit;
import com.tr.auth.entity.User;
import com.tr.auth.kit.JwtKit;
import com.tr.auth.repository.UserRepository;
import com.tr.auth.service.UserService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author: TR
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;
    @Resource
    private TokenEndpoint tokenEndpoint;
    @Resource
    private TokenStore tokenStore;
    @Resource
    private DefaultTokenServices defaultTokenServices;

    @Override
    public OAuth2AccessToken login(String username, String password) {
        CusAuthentication cusAuthentication = new CusAuthentication();
        cusAuthentication.setName("auth"); // 传 oauth_client_details 表的 client_id
        cusAuthentication.setAuthenticated(true);
        Map<String, String> params = Maps.newLinkedHashMap();
        params.put("grant_type", "password");
        params.put("username", username);
        params.put("password", password);
        try {
            OAuth2AccessToken accessToken = tokenEndpoint.postAccessToken(cusAuthentication, params).getBody();
            return accessToken;
        } catch (HttpRequestMethodNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordEncoderKit.encode(password));
        return userRepository.save(user);
    }

    @Override
    public boolean logout() {
        tokenStore.removeAccessToken(tokenStore.readAccessToken(JwtKit.getAuthorization()));
        return defaultTokenServices.revokeToken(JwtKit.getAuthorization());
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
