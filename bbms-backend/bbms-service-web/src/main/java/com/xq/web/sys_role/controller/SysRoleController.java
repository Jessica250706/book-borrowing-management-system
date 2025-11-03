package com.xq.web.sys_role.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.sys_role.entity.RoleParam;
import com.xq.web.sys_role.entity.SysRole;
import com.xq.web.sys_role.service.SysRoleService;
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
     * @param role 角色参数
     * @return
     */
    @PostMapping
    public ResultVo addRole(@RequestBody SysRole role){
        role.setCreateTime(new Date());
        boolean save = sysRoleService.save(role);
        if (save){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }

    /**
     * 修改角色
     * @param role
     * @return
     */
    @PutMapping("/{roleId}")
    public ResultVo editRole(@RequestBody SysRole role){
        role.setUpdateTime(new Date());
        boolean save = sysRoleService.updateById(role);
        if (save){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    /**
     * 删除角色
     * @param roleId
     * @return
     */
    @DeleteMapping("/{roleId}")
    public ResultVo deleteRole(@PathVariable("roleId") Long roleId){
        boolean b = sysRoleService.removeById(roleId);
        if (b){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }

    /**
     * 获取角色列表
     * @param param
     * @return
     */
    @GetMapping("/list")
    public ResultVo getList(RoleParam param){
        IPage<SysRole> list = sysRoleService.list(param);
        return ResultUtils.success("查询成功", list);
    }
}
