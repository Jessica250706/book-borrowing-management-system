package com.xq.web.sys_user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xq.pojo.sys_role.SelectType;
import com.xq.pojo.sys_user.PageParam;
import com.xq.pojo.sys_user.SysUser;
import com.xq.pojo.sys_user_role.SysUserRole;
import com.xq.service.sys_user.SysUserService;
import com.xq.service.sys_user_role.SysUserRoleService;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Reference
    private SysUserService sysUserService;

    @Reference
    private SysUserRoleService sysUserRoleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    //新增员工
    @PostMapping
    public ResultVo addUser(@RequestBody SysUser sysUser){
        //判断用户名是否存在
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getUsername,sysUser.getUsername());
        SysUser one = sysUserService.getOne(query);
        if(one != null){
            return ResultUtils.error("用户名已经存在!");
        }
        //密码加密
        /*if(StringUtils.isNotEmpty(sysUser.getPassword())){
            sysUser.setPassword(DigestUtils.md5DigestAsHex(sysUser.getPassword().getBytes()));
        }*/
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        sysUser.setIsAdmin("0");
        sysUser.setCreateTime(new Date());
        //存储到数据库中
        boolean save = sysUserService.save(sysUser);
        if(save){
            return ResultUtils.success("新增用户成功");
        }
        return ResultUtils.error("新增用户失败");
    }

    //编辑员工
    @PutMapping
    public ResultVo editUser(@RequestBody SysUser sysUser){
        //判断用户信息是否存在
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getUsername,sysUser.getUsername());
        SysUser one = sysUserService.getOne(query);
        if(one != null && one.getUserId() != sysUser.getUserId()){
            return ResultUtils.error("账户已经被占用");
        }
        //密码加密
        if(StringUtils.isNotEmpty(sysUser.getPassword())){
            sysUser.setPassword(DigestUtils.md5DigestAsHex(sysUser.getPassword().getBytes()));
        }
        sysUser.setUpdateTime(new Date());
        //存入数据库
        boolean save = sysUserService.updateById(sysUser);
        if(save){
            return ResultUtils.success("编辑用户成功");
        }
        return ResultUtils.error("编辑用户失败");
    }

    //删除用户
    @DeleteMapping("/{userId}")
    public ResultVo deleteUser(@PathVariable("userId") Long userId){
        boolean remove = sysUserService.removeById(userId);
        if(remove){
            return ResultUtils.success("删除用户成功");
        }
        return ResultUtils.error("删除用户失败");
    }

    //查询用户列表
    @GetMapping("/list")
    public ResultVo getList(PageParam param){
        IPage<SysUser> list = sysUserService.list(param);
        //查询出来不显示密码
        list.getRecords().stream().forEach(item ->{
            item.setPassword("");
        });
        return ResultUtils.success("用户查询成功",list);
    }

    //根据用户id查询角色id
    @GetMapping("/role")
    public ResultVo getRole(Long userId){
        QueryWrapper<SysUserRole> query = new QueryWrapper<>();
        query.lambda().eq(SysUserRole::getUserId,userId);
        SysUserRole one = sysUserRoleService.getOne(query);
        return ResultUtils.success("查询成功",one);
    }

    //查询课程教师
    @GetMapping("getTeacher")
    public ResultVo getTeacher(){
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getUserType,"2");
        List<SysUser> list = sysUserService.list(query);
        //组装数据
        List<SelectType> selectTypeList = new ArrayList<>();
        if(list.size() > 0){
            list.stream().forEach(item ->{
                SelectType selectType = new SelectType();
                selectType.setLabel(item.getNickName());
                selectType.setValue(item.getUserId());
                selectTypeList.add(selectType);
            });
        }
        return ResultUtils.success("查询成功",selectTypeList);
    }
}
