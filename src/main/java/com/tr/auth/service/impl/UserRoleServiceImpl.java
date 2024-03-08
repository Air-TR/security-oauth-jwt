package com.tr.auth.service.impl;

import com.tr.auth.repository.UserRoleRepository;
import com.tr.auth.service.UserRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: TR
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Resource
    private UserRoleRepository userRoleRepository;

    @Override
    public List<String> findRolenameListByUsername(String username) {
        return userRoleRepository.findRolenameListByUsername(username);
    }

}
