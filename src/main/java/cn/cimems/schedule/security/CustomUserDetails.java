package cn.cimems.schedule.security;

import cn.cimems.schedule.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将用户角色转为 Spring Security 的权限
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 使用邮箱作为登录标识
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 简单起见，始终返回 true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 简单起见，始终返回 true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 简单起见，始终返回 true
    }

    @Override
    public boolean isEnabled() {
        return user.getApproved(); // 根据用户是否审核决定是否启用
    }

    public User getUser() {
        return user;
    }
}
