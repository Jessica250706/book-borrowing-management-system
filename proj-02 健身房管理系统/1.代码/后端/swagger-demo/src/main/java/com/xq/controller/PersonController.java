package com.xq.controller;

import com.xq.annotation.NotIncludeSwagger;
import com.xq.pojo.Person;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("person")
@Api(tags = {"描述Person的控制器类"},description = "Person类的描述信息")
public class PersonController {

    @GetMapping("findPerson")
    @ApiOperation(value = "根据条件查询Person信息的方法",notes = "必须携带id,username参数")
    @ApiImplicitParam(name = "address",value = "地址",required = true,paramType = "query",dataType = "string")
    public Person findPerson(@RequestParam("id") Integer id,
                             @RequestParam("username")
                             @ApiParam(name = "用户名",value = "用户的姓名",required = true) String username,
                             @RequestParam("address") String address){
        Person person = new Person();
        person.setId(id);
        person.setUsername(username);
        person.setAddress("USA");
        return person;
    }

    @PostMapping("add")
    //@NotIncludeSwagger //不需要生成对应控制器方法的接口文档
    @ApiIgnore
    public Person add(@RequestBody Person person){
        return person;
    }

}


