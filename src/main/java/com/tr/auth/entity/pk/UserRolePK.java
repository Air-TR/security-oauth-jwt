package com.tr.auth.entity.pk;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: TR
 */
@Data
public class UserRolePK implements Serializable {

    private String username;
    private String rolename;

}
