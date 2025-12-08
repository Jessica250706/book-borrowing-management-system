package com.xq.web.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.system.user.entity.SysUserRole;
import com.xq.web.system.user.mapper.SysUserRoleMapper;
import com.xq.web.system.user.service.SysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoleToUser(Long userId, Long roleId) {
        try {
            System.out.println("开始为用户分配角色: userId=" + userId + ", roleId=" + roleId);

            // 先检查是否已存在关联
            LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUserRole::getUserId, userId)
                    .eq(SysUserRole::getRoleId, roleId);

            long count = this.count(queryWrapper);
            System.out.println("现有角色关联数量: " + count);

            if (count > 0) {
                System.out.println("用户-角色关联已存在，无需重复添加");
                return true;
            }

            // 创建新的用户角色关联
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            // createTime由FieldFill.INSERT自动填充

            System.out.println("创建用户角色关联对象: " + userRole);

            boolean saveResult = this.save(userRole);

            if (saveResult) {
                System.out.println("用户角色关联保存成功");
                // 验证保存结果
                SysUserRole savedRole = this.getById(userRole.getUserRoleId());
                System.out.println("保存后的记录: " + savedRole);
            } else {
                System.out.println("用户角色关联保存失败");
            }

            return saveResult;

        } catch (Exception e) {
            System.err.println("分配角色失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("分配角色失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean updateUserRole(Long userId, Long newRoleId) {
        // 先删除用户的所有角色关联
        LambdaQueryWrapper<SysUserRole> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysUserRole::getUserId, userId);
        this.remove(deleteWrapper);

        // 添加新的角色关联
        return assignRoleToUser(userId, newRoleId);
    }

    @Override
    @Transactional
    public boolean removeUserRoles(Long userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        return this.remove(queryWrapper);
    }

    @Override
    public boolean hasRole(Long userId, Long roleId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getRoleId, roleId);
        return this.count(queryWrapper) > 0;
    }
}
