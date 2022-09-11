package com.jwt.crud.security.dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtDto {
    private String token;
    private String bearer = "Bearer";
    private String NameUser;
    private Collection<? extends GrantedAuthority> authorities;

    public JwtDto(String token, String nameUser, Collection<? extends GrantedAuthority> authorities) {
        this.token = token;
        this.NameUser = nameUser;
        this.authorities = authorities;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBearer() {
        return bearer;
    }

    public void setBearer(String bearer) {
        this.bearer = bearer;
    }

    public String getNameUser() {
        return NameUser;
    }

    public void setNameUser(String nameUser) {
        this.NameUser = nameUser;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}
