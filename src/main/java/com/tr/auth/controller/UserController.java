package com.tr.auth.controller;

import com.tr.auth.entity.User;
import com.tr.auth.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author: TR
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

}
