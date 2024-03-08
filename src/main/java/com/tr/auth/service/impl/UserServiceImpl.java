package com.tr.auth.service.impl;

import com.google.common.collect.Maps;
import com.tr.auth.config.CusAuthentication;
import com.tr.auth.constant.RedisKey;
import com.tr.auth.kit.PasswordEncoderKit;
import com.tr.auth.entity.User;
import com.tr.auth.kit.JwtKit;
import com.tr.auth.repository.UserRepository;
import com.tr.auth.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: TR
 */
@Service
public class UserServiceImpl implements UserService {

    @Value("${token.alive-time}")
    private Long tokenAliveTime;

    @Resource
    private UserRepository userRepository;
    @Resource
    private TokenEndpoint tokenEndpoint;
    @Resource
    private TokenStore tokenStore;
//    @Resource
//    private DefaultTokenServices defaultTokenServices;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
            stringRedisTemplate.opsForValue().set(RedisKey.TOKEN + JwtKit.getUsername(accessToken.getValue()), accessToken.getValue(), tokenAliveTime, TimeUnit.SECONDS);
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

    /**
     * 项目引入了 JWT，在实现退出功能的时候，发现即使调用了相关接口废弃令牌，但是令牌仍然可以使用。
     *  查看原码才知道，使用的 JwtTokenStore 的 removeAccessToken 是个空方法。
     *
     * 查阅资料，其实要完美地失效JWT是没办法做到的。
     *  “Actually, JWT serves a different purpose than a session and it is not possible to forcefully delete or invalidate an existing token.”
     *                                                                                 ———— 实际上，JWT的作用与会话不同，不可能强制删除或使现有令牌无效。
     * 有以下几个方法可以做到失效 JWT token：
     *  1.将 token 存入 DB（如 Redis）中，失效则删除；但增加了一个每次校验时候都要先从 DB 中查询 token 是否存在的步骤，而且违背了 JWT 的无状态原则（这不就和 session 一样了么？）
     *  2.维护一个 token 黑名单，失效则加入黑名单中
     *  3.在 JWT 中增加一个版本号字段，失效则改变该版本号
     *  4.在服务端设置加密的 key 时，为每个用户生成唯一的 key，失效则改变该 key
     *
     * 最后决定使用 Redis 建立一个白名单，大体思路如下：
     *  1.生成 Jwt 的时候，将 Jwt Token 存入 redis 中
     *  2.扩展 Jwt 的验证功能，验证 redis 中是否存在数据，如果存在则 token 有效，否则无效
     *  3.实 现removeAccessToken 功能，在该方法内删除 redis 对应的 key
     */
    @Override
    public boolean logout() {
//        defaultTokenServices.revokeToken(JwtKit.getAuthorization()); // 这一步其实没啥用，并不会从框架中 remove 掉 token，原来的 token 还能继续使用
        return stringRedisTemplate.delete(RedisKey.TOKEN + JwtKit.getUsername());
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
