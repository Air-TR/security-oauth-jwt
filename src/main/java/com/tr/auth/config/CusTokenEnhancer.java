package com.tr.auth.config;

import com.google.common.collect.Maps;
import com.tr.auth.entity.User;
import com.tr.auth.service.UserService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author: TR
 */
public class CusTokenEnhancer implements TokenEnhancer {

    @Resource
    private UserService userService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        if ((accessToken instanceof DefaultOAuth2AccessToken)) {
            DefaultOAuth2AccessToken token = ((DefaultOAuth2AccessToken) accessToken);
            token.setAdditionalInformation(getAdditionalInfo(authentication)); // 给 token 添加额外信息（自定义信息）
            return token;
        }
        return accessToken;
    }

    private Map<String, Object> getAdditionalInfo(OAuth2Authentication authentication) {
        String username = ((org.springframework.security.core.userdetails.User) authentication
                .getUserAuthentication().getPrincipal()).getUsername();
        User user = userService.findByUsername(username);
        Map<String, Object> map = Maps.newHashMap();
        map.put("username", user.getUsername());
        map.put("phone", user.getPhone());
        map.put("authorities", authentication.getAuthorities());
        return map;
    }

}
