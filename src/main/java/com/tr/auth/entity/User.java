package com.tr.auth.entity;

import com.tr.auth.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @Author: TR
 */
@ApiModel(value = "用户")
@Data
@Entity
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class User extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "用户名")
    @Column(length = 32, nullable = false, unique = true)
    private String username;
    @ApiModelProperty(value = "密码")
    @Column(nullable = false)
    private String password;
    @ApiModelProperty(value = "手机号")
    @Column(unique = true)
    private String phone;

}
