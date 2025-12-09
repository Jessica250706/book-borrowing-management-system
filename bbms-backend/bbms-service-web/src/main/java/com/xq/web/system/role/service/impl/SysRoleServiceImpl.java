package com.xq.web.system.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.dto.PageDTO;
import com.xq.web.system.role.dto.SysRoleDetailDTO;
import com.xq.web.system.role.entity.RoleParam;
import com.xq.web.system.role.entity.SysRole;
import com.xq.web.system.role.mapper.SysRoleMapper;
import com.xq.web.system.role.service.SysRoleService;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public PageDTO<SysRoleDetailDTO> list(RoleParam param) {
        // 创建MyBatis Plus分页对象
        Page<SysRole> page = new Page<>(param.getCurrentPage(), param.getPageSize());

        // 构造查询条件
        QueryWrapper<SysRole> query = new QueryWrapper<>();

        // 执行分页查询
        Page<SysRole> result = this.baseMapper.selectPage(page, query);

        // 将SysRole实体转换为SysRoleDetailDTO
        List<SysRoleDetailDTO> records = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 创建并返回自定义的PageDTO
        return PageDTO.of(
                result.getCurrent(),
                result.getSize(),
                result.getTotal(),
                records
        );
    }

    @Override
    public SysRoleDetailDTO getRoleDetailById(Long roleId) {
        if (roleId == null) {
            throw new RuntimeException("角色ID不能为空");
        }

        // 查询实体
        SysRole sysRole = this.getById(roleId);
        if (sysRole == null) {
            throw new RuntimeException("角色不存在");
        }

        // 转换为DTO
        return convertToDTO(sysRole);
    }

    @Override
    public SysRoleDetailDTO getRoleDetailByCode(String roleCode) {
        if (roleCode == null || roleCode.trim().isEmpty()) {
            throw new RuntimeException("角色编码不能为空");
        }

        // 构造查询条件
        QueryWrapper<SysRole> query = new QueryWrapper<>();
        query.lambda().eq(SysRole::getRoleCode, roleCode);

        // 查询实体
        SysRole sysRole = this.getOne(query);
        if (sysRole == null) {
            throw new RuntimeException("角色不存在");
        }

        // 转换为DTO
        return convertToDTO(sysRole);
    }

    /**
     * 将SysRole实体转换为SysRoleDetailDTO
     */
    private SysRoleDetailDTO convertToDTO(SysRole sysRole) {
        if (sysRole == null) {
            return null;
        }

        SysRoleDetailDTO dto = new SysRoleDetailDTO();
        dto.setRoleId(sysRole.getRoleId());
        dto.setRoleCode(sysRole.getRoleCode());
        dto.setRoleName(sysRole.getRoleName());
        dto.setMaxBorrowNum(sysRole.getMaxBorrowNum());
        dto.setMaxBorrowDays(sysRole.getMaxBorrowDays());
        dto.setMaxRenewDays(sysRole.getMaxRenewDays());
        dto.setRemark(sysRole.getRemark());

        return dto;
    }
}
