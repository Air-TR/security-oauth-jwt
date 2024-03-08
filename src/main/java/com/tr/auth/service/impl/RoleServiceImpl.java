package com.tr.auth.service.impl;

import com.google.common.collect.Lists;
import com.tr.auth.entity.Role;
import com.tr.auth.kit.StringKit;
import com.tr.auth.repository.RoleRepository;
import com.tr.auth.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * @Author: TR
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleRepository roleRepository;

    @Override
    public Role findById(String id) {
        return roleRepository.findById(id).orElseThrow(() -> new RuntimeException("not find by id: " + id));
    }

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void deleteById(String id) {
        roleRepository.deleteById(id);
    }

    @Override
    public List<Role> findList(Role role) {
        return roleRepository.findAll(getSpecification(role));
    }

    @Override
    public Page<Role> findPage(Role role, Pageable page) {
        return roleRepository.findAll(getSpecification(role), page);
    }

    private Specification<Role> getSpecification(Role role) {
        return (root, query, builder) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringKit.isNotBlank(role.getRolename())) {
                predicates.add(builder.like(root.get("rolename"), "%" + role.getRolename() + "%"));
            }
            if (StringKit.isNotBlank(role.getDescription())) {
                predicates.add(builder.like(root.get("description"), "%" + role.getDescription() + "%"));
            }
            predicates.add(builder.isFalse(root.get("deleteFlag")));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
