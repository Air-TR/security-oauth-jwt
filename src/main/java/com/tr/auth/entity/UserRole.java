package com.tr.auth.entity;

import com.tr.auth.entity.pk.UserRolePK;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

/**
 * @Author: TR
 */
@Data
@Entity
@IdClass(UserRolePK.class)
public class UserRole implements Serializable {

    @Id
    private String username;
    @Id
    private String rolename;

}
