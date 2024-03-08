package com.tr.auth.repository;

import com.tr.auth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, String>, JpaSpecificationExecutor<UserRole> {

    @Transactional
    void deleteByUsername(String username);

    @Query(value = "select rolename from user_role where username = :username", nativeQuery = true)
    List<String> findRolenameListByUsername(String username);
    
}
