package com.xq.web.member_course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xq.web.course.entity.Course;
import com.xq.web.course.service.CourseService;
import com.xq.web.member.entity.RechargeParam;
import com.xq.web.member.mapper.MemberMapper;
import com.xq.web.member_course.entity.MemberCourse;
import com.xq.web.member_course.mapper.MemberCourseMapper;
import com.xq.web.member_course.service.MemberCourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberCourseServiceImpl extends ServiceImpl<MemberCourseMapper, MemberCourse> implements MemberCourseService {

    @Autowired
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
