package com.xq.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.mapper.MemberCourseMapper;
import com.xq.mapper.MemberMapper;
import com.xq.pojo.course.Course;
import com.xq.pojo.member.RechargeParam;
import com.xq.pojo.member_course.MemberCourse;
import com.xq.service.course.CourseService;
import com.xq.service.member_course.MemberCourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberCourseServiceImpl extends ServiceImpl<MemberCourseMapper, MemberCourse> implements MemberCourseService {

    @Reference
    CourseService courseService;

    @Autowired
    MemberMapper memberMapper;
    @Override
    @Transactional
    public void joinCourse(MemberCourse memberCourse) {
        //根据课程id查询课程信息
        Course course = courseService.getById(memberCourse.getCourseId());
        BeanUtils.copyProperties(course,memberCourse);
        //插入报名表
        int insert = this.baseMapper.insert(memberCourse);
        if(insert > 0){ //报名成功，执行充值金额的扣钱
            RechargeParam param = new RechargeParam();
            param.setMemberId(memberCourse.getMemberId());
            param.setMoney(course.getCoursePrice());
            memberMapper.subMoney(param);
        }

    }
}
