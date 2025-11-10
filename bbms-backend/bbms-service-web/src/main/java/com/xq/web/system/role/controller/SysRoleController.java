package com.xq.web.system.role.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.system.role.entity.RoleParam;
import com.xq.web.system.role.entity.SysRole;
import com.xq.web.system.role.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 系统角色
 */
@RestController
@RequestMapping("/api/role")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 新增角色
     */
    @PostMapping
    public ResultVo<SysRole> addRole(@RequestBody SysRole role){
        role.setCreateTime(new Date());
        boolean save = sysRoleService.save(role);
        if (save){
            return ResultUtils.successMsg("新增成功!");
        }
        return ResultUtils.errorMsg("新增失败!");
    }

    /**
     * 修改角色
     */
    @PutMapping("/{roleId}")
    public ResultVo<SysRole> editRole(@RequestBody SysRole role, @PathVariable("roleId") Long roleId){
        role.setRoleId(roleId);
        role.setUpdateTime(new Date());
        boolean save = sysRoleService.updateById(role);
        if (save){
            return ResultUtils.successMsg("编辑成功!");
        }
        return ResultUtils.errorMsg("编辑失败!");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleId}")
    public ResultVo<SysRole> deleteRole(@PathVariable("roleId") Long roleId){
        boolean b = sysRoleService.removeById(roleId);
        if (b){
            return ResultUtils.successMsg("删除成功!");
        }
        return ResultUtils.errorMsg("删除失败!");
    }

    /**
     * 获取角色列表
     */
    @GetMapping("/list")
    public ResultVo<IPage<SysRole>> getList(RoleParam param){
        IPage<SysRole> list = sysRoleService.list(param);
        return ResultUtils.success("查询成功", list);
    }
}
