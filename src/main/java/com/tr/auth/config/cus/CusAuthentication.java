package com.tr.auth.config.cus;

import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * @Author: TR
 */
@Data
public class CusAuthentication implements Authentication {

    private String name;
    private Boolean authenticated;
    private List<? extends GrantedAuthority> authorities;
    private Object credentials;
    private Object details;
    private Object principal;

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

}
