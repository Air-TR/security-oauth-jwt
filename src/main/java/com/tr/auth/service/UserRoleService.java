package com.tr.auth.service;

import java.util.List;

public interface UserRoleService {

    List<String> findRolenameListByUsername(String username);

}
