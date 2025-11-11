package com.xq.config.springSecurity;

import com.xq.web.system.user.entity.SysUser;
import com.xq.web.system.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义UserDetailsService
 */
@Component("customizedUserDetailService")
public class CustomizedUserDetailService implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 格式: username:roleCode
        String[] parts = username.split(":");
        if (parts.length != 2) {
            throw new UsernameNotFoundException("用户名格式错误");
        }

        String actualUsername = parts[0];
        String roleCode = parts[1];

        // 查询用户信息
        SysUser sysUser = sysUserService.getUserByUsername(actualUsername);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 构建权限列表
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 根据角色代码添加权限
        if ("ADMIN".equals(roleCode) || "SYS_ADMIN".equals(roleCode)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            authorities.add(new SimpleGrantedAuthority("system:user:list"));
            authorities.add(new SimpleGrantedAuthority("system:user:add"));
            authorities.add(new SimpleGrantedAuthority("system:user:edit"));
            authorities.add(new SimpleGrantedAuthority("system:user:delete"));
            authorities.add(new SimpleGrantedAuthority("system:book:manage"));
            authorities.add(new SimpleGrantedAuthority("system:borrow:manage"));
        } else if ("USER".equals(roleCode)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            authorities.add(new SimpleGrantedAuthority("system:book:list"));
            authorities.add(new SimpleGrantedAuthority("system:borrow:apply"));
            authorities.add(new SimpleGrantedAuthority("system:borrow:self"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_GUEST"));
        }

        // 返回UserDetails
        return new User(
                sysUser.getUsername(),
                sysUser.getPassword(),
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}