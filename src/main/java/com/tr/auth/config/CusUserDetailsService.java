package com.tr.auth.config;

import com.tr.auth.entity.User;
import com.tr.auth.service.UserRoleService;
import com.tr.auth.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @Author TR
 */
@Service
public class CusUserDetailsService implements UserDetailsService {

    @Resource
    private UserService userService;
    @Resource
    private UserRoleService userRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        // 获取用户权限（注：权限一定要以 "ROLE_" 开头，如果数据库中存储的 role_name 没有 "ROLE_" 前缀，则在这里加上，如果有，则不作处理）
        List<String> rolenameList = userRoleService.findRolenameListByUsername(username);
        List<SimpleGrantedAuthority> authorities = rolenameList.stream().map(rolename -> new SimpleGrantedAuthority("ROLE_".concat(rolename))).collect(toList());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities); // 这里 authorities 不能传 null，否则登录也会返回 403，即使登录配置了白名单
    }

}
