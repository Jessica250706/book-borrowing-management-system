package com.xq.web.system.role.controller;

import com.xq.dto.PageDTO;
import com.xq.dto.PageParam;
import com.xq.utils.ResultUtils;
import com.xq.dto.ResultVo;
import com.xq.web.system.role.dto.SysRoleDetailDTO;
import com.xq.web.system.role.entity.SysRole;
import com.xq.web.system.role.service.SysRoleService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统角色
 * @module 系统角色
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
     * 获取角色列表（分页）
     */
    @GetMapping("/list")
    public ResultVo<PageDTO<SysRoleDetailDTO>> getList(PageParam param){
        PageDTO<SysRoleDetailDTO> list = sysRoleService.list(param);
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 获取角色详情
     * 根据角色ID获取角色详情
     */
    @GetMapping("/{roleId}")
    public ResultVo<SysRoleDetailDTO> getRoleDetail(
            @Parameter(description = "角色ID", required = true)
            @PathVariable("roleId") Long roleId) {
        try {
            SysRoleDetailDTO role = sysRoleService.getRoleDetailById(roleId);
            return ResultUtils.success("查询成功", role);
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        } catch (Exception e) {
            return ResultUtils.errorMsg("查询角色详情失败");
        }
    }

    /**
     * 根据角色编码获取角色详情
     * 根据角色编码获取角色详细信息
     */
    @GetMapping("/code/{roleCode}")
    public ResultVo<SysRoleDetailDTO> getRoleDetailByCode(
            @Parameter(description = "角色编码", required = true)
            @PathVariable("roleCode") String roleCode) {
        try {
            SysRoleDetailDTO role = sysRoleService.getRoleDetailByCode(roleCode);
            return ResultUtils.success("查询成功", role);
        } catch (RuntimeException e) {
            return ResultUtils.errorMsg(e.getMessage());
        } catch (Exception e) {
            return ResultUtils.errorMsg("查询角色详情失败");
        }
    }

    /**
     * 检查角色是否存在
     * 检查指定角色ID是否存在
     */
    @GetMapping("/check/{roleId}")
    public ResultVo<Boolean> checkRoleExists(
            @Parameter(description = "角色ID", required = true)
            @PathVariable("roleId") Long roleId) {
        try {
            SysRoleDetailDTO role = sysRoleService.getRoleDetailById(roleId);
            return ResultUtils.success("角色存在", true);
        } catch (RuntimeException e) {
            return ResultUtils.success("角色不存在", false);
        } catch (Exception e) {
            return ResultUtils.errorMsg("检查角色失败");
        }
    }
}
