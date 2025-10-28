package com.xq.service.member_course;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xq.pojo.member_course.MemberCourse;

public interface MemberCourseService extends IService<MemberCourse> {

    void joinCourse(MemberCourse memberCourse);
}
