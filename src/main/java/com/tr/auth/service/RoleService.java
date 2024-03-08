package com.tr.auth.service;

import com.tr.auth.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {

    Role findById(String id);
    Role save(Role role);
    void deleteById(String id);
    List<Role> findList(Role role);
    Page<Role> findPage(Role role, Pageable page);

}
