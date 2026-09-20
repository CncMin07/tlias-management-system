package com.fallenleaves.service;


import com.fallenleaves.pojo.PageResult;
import com.fallenleaves.pojo.Student;
import com.fallenleaves.pojo.StudentQueryParam;

import java.util.List;

public interface StudentService {

    PageResult<Student> page(StudentQueryParam studentQueryParam);

    void deleteByIds(List<Integer> ids);

    void save(Student student);

    Student findById(Integer id);

    void update(Student student);

    void violate(Integer id, Integer score);
}
