package com.xq.web.system.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.system.role.entity.RoleParam;
import com.xq.web.system.role.entity.SysRole;
import com.xq.web.system.role.mapper.SysRoleMapper;
import com.xq.web.system.role.service.SysRoleService;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Override
    public IPage<SysRole> list(RoleParam param) {
        // 构造分页对象
        IPage<SysRole> page = new Page<>();
        page.setSize(param.getPageSize());
        page.setCurrent(param.getCurrentPage());
        // 构造查询条件
        QueryWrapper<SysRole> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(param.getRoleName())){
            query.lambda().like(SysRole::getRoleName, param.getRoleName());
        }
        return this.baseMapper.selectPage(page, query);
    }

    @Override
    public SysRole getRoleDetailById(Long roleId) {
        if (roleId == null) {
            throw new RuntimeException("角色ID不能为空");
        }

        // 使用Mapper中的方法查询
        SysRole role = this.baseMapper.selectByRoleId(roleId);

        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        return role;
    }

    @Override
    public SysRole getRoleDetailByCode(String roleCode) {
        if (roleCode == null || roleCode.trim().isEmpty()) {
            throw new RuntimeException("角色编码不能为空");
        }

        // 使用Mapper中的方法查询
        SysRole role = this.baseMapper.selectByRoleCode(roleCode);

        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        return role;
    }
}
