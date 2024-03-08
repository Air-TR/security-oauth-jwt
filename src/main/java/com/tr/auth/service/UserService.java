package com.tr.auth.service;

import com.tr.auth.entity.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * @Author: TR
 */
public interface UserService {

    OAuth2AccessToken login(String username, String password);
    OAuth2AccessToken refreshToken(String refreshToken);
    User register(String username, String password);

    boolean logout();

    User findByUsername(String username);

}
