package com.xq.web.login.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.xq.jwt.JwtUtils;
import com.xq.utils.ResultUtils;
import com.xq.utils.ResultVo;
import com.xq.web.login.entity.InfoParam;
import com.xq.web.login.entity.LoginParm;
import com.xq.web.login.entity.LoginResult;
import com.xq.web.login.entity.UserInfo;
import com.xq.web.member.entity.Member;
import com.xq.web.member.service.MemberService;
import com.xq.web.sys_menu.entity.MakeMenuTree;
import com.xq.web.sys_menu.entity.RouterVo;
import com.xq.web.sys_menu.entity.SysMenu;
import com.xq.web.sys_menu.service.SysMenuService;
import com.xq.web.sys_user.entity.SysUser;
import com.xq.web.sys_user.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    //生成验证码
    @PostMapping("/image")
    public ResultVo imageCode(HttpServletRequest request) throws IOException{
        //获取验证码字符
        String text = defaultKaptcha.createText();
        //将验证码存储到session里面
        HttpSession session = request.getSession();
        session.setAttribute("code",text);
        //生成图片
        BufferedImage bufferedImage = defaultKaptcha.createImage(text);
        ByteArrayOutputStream outputStream = null;
        try{
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"jpg",outputStream);
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(outputStream.toByteArray());
            //System.out.println("base64:" + base64);
            String captchaBase64 = "data:image/jpeg;base64," + base64.replaceAll("\r\n", "");
            //System.out.println("code:" + captchaBase64);
            ResultVo result = new ResultVo("生成成功",200,captchaBase64);
            return result;
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(outputStream != null){
                outputStream.close();
            }
        }
        return null;
    }

    @Autowired
    MemberService memberService;

    @Autowired
    SysUserService sysUserService;
    @Autowired
    JwtUtils jwtUtils;

    //登录
    /*@PostMapping("/login")
    public ResultVo login(HttpServletRequest request, @RequestBody LoginParm loginParam){
        //获取sessoin
        HttpSession session = request.getSession();
        //获取验证码
        String code = (String) session.getAttribute("code");
        //验证验证码
        if(!code.equals(loginParam.getCode())){
            return ResultUtils.error("验证码错误!");
        }
        String password = DigestUtils.md5DigestAsHex(loginParam.getPassword().getBytes());
        //用户类型判断
        if(loginParam.getUserType().equals("1")){ //会员
            //构造查询条件
            QueryWrapper<Member> query = new QueryWrapper<>();
            query.lambda().eq(Member::getUsername,loginParam.getUsername()).eq(Member::getPassword,loginParam.getPassword());
            Member one = memberService.getOne(query);
            if(one == null){
                return ResultUtils.error("用户名或密码错误!");
            }
            //生成token
            Map<String,String> map = new HashMap<>();
            map.put("userId",Long.toString(one.getMemberId()));
            map.put("username",one.getUsername());
            String token = jwtUtils.generateToken(map);
            System.out.println("token:" + token);
            //返回登录成功信息
            LoginResult result = new LoginResult();
            result.setToken(token);
            result.setUserId(one.getMemberId());
            result.setUsername(one.getName());
            result.setUserType(loginParam.getUserType());
            return ResultUtils.success("登录成功",result);
        }else if(loginParam.getUserType().equals("2")){ //员工
            QueryWrapper<SysUser> query = new QueryWrapper<>();
            query.lambda().eq(SysUser::getPassword,password).eq(SysUser::getUsername,loginParam.getUsername());
            SysUser one = sysUserService.getOne(query);
            if(one == null){
                return ResultUtils.error("用户名或密码错误!");
            }
            //生成token
            Map<String,String> map = new HashMap<>();
            map.put("userId",Long.toString(one.getUserId()));
            map.put("username",one.getUsername());
            String token = jwtUtils.generateToken(map);
            System.out.println("token:" + token);
            //返回登录成功信息
            LoginResult result = new LoginResult();
            result.setToken(token);
            result.setUserId(one.getUserId());
            result.setUsername(one.getNickName());
            result.setUserType(loginParam.getUserType());
            return ResultUtils.success("登录成功",result);
        }else{
            return ResultUtils.error("用户类型错误!");
        }
    }*/

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResultVo login(HttpServletRequest request, @RequestBody LoginParm param){
        if(StringUtils.isEmpty(param.getUsername()) ||
                StringUtils.isEmpty(param.getPassword()) ||
                StringUtils.isEmpty(param.getUserType()) ||
                StringUtils.isEmpty(param.getCode())){
            return ResultUtils.error("用户名、密码、验证码或用户类型不能为空!");
        }
        //获取Session
        HttpSession session = request.getSession();
        //获取验证码
        String code = (String) session.getAttribute("code");
        if(StringUtils.isEmpty(code)){
            return ResultUtils.error("验证码过期");
        }
        //验证验证码
        if(!code.equals(param.getCode())){
            return ResultUtils.error("验证码错误");
        }
        String password =  passwordEncoder.encode(param.getPassword());
        //构造SpringSecurity需要的token
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(param.getUsername() + ":" + param.getUserType(), param.getPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        //将认证对象放在SpringSecurity上下文环境中
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        if(param.getUserType().equals("1")){ // 会员
            Member user = (Member) authenticate.getPrincipal();
            //生成token
            Map<String,String> map = new HashMap<>();
            map.put("userId",Long.toString(user.getMemberId()));
            map.put("username",user.getUsername());
            map.put("userType",param.getUserType());
            String token = jwtUtils.generateToken(map);
            //返回登录成功信息
            LoginResult result = new LoginResult();
            result.setToken(token);
            result.setUserId(user.getMemberId());
            result.setUsername(user.getName());
            result.setUserType(param.getUserType());
            return ResultUtils.success("登录成功",result);
        }else if(param.getUserType().equals("2")){ // 员工
            SysUser user = (SysUser) authenticate.getPrincipal();
            //生成token
            Map<String,String> map = new HashMap<>();
            map.put("userId",Long.toString(user.getUserId()));
            map.put("username",user.getUsername());
            map.put("userType",param.getUserType());
            String token = jwtUtils.generateToken(map);
            //返回登录成功信息
            LoginResult result = new LoginResult();
            result.setToken(token);
            result.setUserId(user.getUserId());
            result.setUsername(user.getNickName());
            result.setUserType(param.getUserType());
            return ResultUtils.success("登录成功",result);
        }else{
            return ResultUtils.error("用户类型错误");
        }
    }

    @Autowired
    SysMenuService sysMenuService;

    //查询用户信息
    @GetMapping("/getInfo")
    public ResultVo getInfo(InfoParam param){
        UserInfo userInfo = new UserInfo();
        if(param.getUserType().equals("1")){ //会员
            //根据会员id查询权限字段
            List<SysMenu> menuList = sysMenuService.getMenuByMemberId(param.getUserId());
            //获取全部的code字段
            List<String> collect = Optional.ofNullable(menuList).orElse(new ArrayList<>())
                    .stream()
                    .map(item -> item.getCode())
                    .filter(item -> item != null)
                    .collect(Collectors.toList());
            //转为数组
            String[] strings = collect.toArray(new String[collect.size()]);
            //查询用户信息
            Member member = memberService.getById(param.getUserId());
            //设置返回信息
            userInfo.setName(member.getName());
            userInfo.setUserId(member.getMemberId());
            userInfo.setPermissions(strings);
            return ResultUtils.success("查询成功",userInfo);
        }else if(param.getUserType().equals("2")){ //员工
            //查询用户信息
            SysUser user = sysUserService.getById(param.getUserId());
            List<SysMenu> menuList = null;
            if(StringUtils.isNotEmpty(user.getIsAdmin()) && user.getIsAdmin().equals("1")){ //超级管理员
                menuList = sysMenuService.list();
            }else{
                menuList = sysMenuService.getMenuByUserId(user.getUserId());
            }
            //获取全部的code字段
            List<String> collect = Optional.ofNullable(menuList).orElse(new ArrayList<>())
                    .stream()
                    .map(item -> item.getCode())
                    .filter(item -> item != null)
                    .collect(Collectors.toList());
            //转为数组
            String[] strings = collect.toArray(new String[collect.size()]);

            //设置返回信息
            userInfo.setName(user.getNickName());
            userInfo.setUserId(user.getUserId());
            userInfo.setPermissions(strings);
            return ResultUtils.success("查询成功",userInfo);
        }else{
            return ResultUtils.error("用户类型错误!");
        }
    }

    @GetMapping("/getMenuList")
    public ResultVo getMenuList(InfoParam parm) {
        System.out.println("infoParam:"+ parm);
        if (parm.getUserType().equals("1")) { //会员
            List<SysMenu> menus = sysMenuService.getMenuByMemberId(parm.getUserId());
            //获取菜单和目录
            List<SysMenu> collect = Optional.ofNullable(menus).orElse(new ArrayList<>())
                    .stream()
                    .filter(item -> item != null && !item.getType().equals("2")).collect(Collectors.toList());
            List<RouterVo> rourer = MakeMenuTree.makeRouter(collect, 0L);
            return ResultUtils.success("查询成功", rourer);
        } else if (parm.getUserType().equals("2")) { //员工
            SysUser user = sysUserService.getById(parm.getUserId());
            List<SysMenu> menuList = null;
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(user.getIsAdmin()) && user.getIsAdmin().equals("1")) {
                menuList = sysMenuService.list();
            } else {
                menuList = sysMenuService.getMenuByUserId(parm.getUserId());
            }
            //获取菜单和目录
            List<SysMenu> collect = Optional.ofNullable(menuList).orElse(new ArrayList<>())
                    .stream()
                    .filter(item -> item != null && !item.getType().equals("2")).collect(Collectors.toList());
            List<RouterVo> rourer = MakeMenuTree.makeRouter(collect, 0L);
            return ResultUtils.success("查询成功", rourer);
        } else {
            return ResultUtils.error("用户类型错误!");
        }
    }
}
