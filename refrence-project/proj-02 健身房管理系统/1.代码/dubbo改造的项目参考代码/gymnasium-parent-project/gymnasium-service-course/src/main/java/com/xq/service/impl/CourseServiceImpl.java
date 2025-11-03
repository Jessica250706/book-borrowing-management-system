package com.xq.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.mapper.CourseMapper;
import com.xq.pojo.course.Course;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements IService<Course> {
}
