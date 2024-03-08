package com.tr.auth.controller;

import com.tr.auth.entity.User;
import com.tr.auth.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author: TR
 *
 * refresh_token 使用：Postman 调用 /oauth/token 接口（TokenEndpoint 中）
 *  1.Authorization 设置 Basic Auth，设置 client 的 username password
 *  2.Body 设置 grant_type & refresh_token
 */
@Api(tags = "用户中心")
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation(value = "登录")
    @PostMapping("/user/login")
    public OAuth2AccessToken login(@RequestParam String username, @RequestParam String password) {
        return userService.login(username, password);
    }

    @ApiOperation(value = "注册")
    @PostMapping("/user/register")
    public User register(@RequestParam String username, @RequestParam String password) {
        return userService.register(username, password);
    }

    @ApiOperation(value = "注销")
    @PostMapping("/user/logout")
    public boolean logout() {
        return userService.logout();
    }

    @GetMapping("/user/test")
    public String test() {
        return "success " + new Date();
    }

    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    @GetMapping("/user/test/role")
    public String testRole() {
        return "role success " + new Date();
    }

}
