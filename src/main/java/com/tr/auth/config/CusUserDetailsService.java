package com.tr.auth.config;

import com.tr.auth.entity.User;
import com.tr.auth.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author TR
 */
@Service
public class CusUserDetailsService implements UserDetailsService {

    @Resource
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(); // 即使没有建 Role 表，authorities 也要生成一个空列表，不能在下一行传 null
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities); // 这里 authorities 不能传 null，否则登录也会返回 403，即使登录配置了白名单
    }
}
