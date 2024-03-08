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
@ApiModel(value = "权限")
@Data
@Entity
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Role extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "角色名称")
    @Column(nullable = false, unique = true)
    private String rolename;
    @ApiModelProperty(value = "描述")
    @Column(nullable = false)
    private String description;

}
