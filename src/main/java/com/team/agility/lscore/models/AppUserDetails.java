package com.team.agility.lscore.models;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.team.agility.lscore.entities.User;

import java.util.List;
import java.util.Objects;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class AppUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String password;

    private Boolean active;
    private Boolean locked;

    private List<SimpleGrantedAuthority> authorities;

    public AppUserDetails(Long id, String email, String username, String password,
        boolean active, boolean locked,
        List<SimpleGrantedAuthority> authorities2) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.active = active;
        this.locked = locked;
        this.authorities = authorities2;
    }

    public AppUserDetails() {
    }

    public static AppUserDetails build(User user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles()
            .forEach(r -> r.getPermissions()
                .forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getName()))));

        log.trace("authorities: {}", user);
        
        return new AppUserDetails(user.getId(), user.getEmail(), user.getUsername(), user.getPassword(), 
            user.getActive(), user.getLocked(),
            authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
            AppUserDetails user = (AppUserDetails) o;
        return Objects.equals(id, user.id);
    }
}