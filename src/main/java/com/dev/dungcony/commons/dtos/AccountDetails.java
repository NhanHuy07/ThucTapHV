package com.dev.dungcony.commons.dtos;

import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.dev.dungcony.modules.auth.enums.Role;
import com.dev.dungcony.modules.users.exceptions.UserProfileNotCreated;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AccountDetails implements UserDetails {

    @Getter
    private final Integer id;
    private final String username;
    @Getter
    private final String email;
    private final Role role;
    @Getter
    private final UUID userUuid;

    /**
     * Trả về userUuid, throw UserProfileNotCreated nếu profile chưa được tạo.
     * Dùng trong các endpoint yêu cầu user đã có profile (cart, order...).
     */
    public UUID requireUserUuid() {
        if (userUuid == null) throw new UserProfileNotCreated();
        return userUuid;
    }

    public AccountDetails(Integer id, String username, String email, Role role, UUID userUuid) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.userUuid = userUuid;
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public @NonNull String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
