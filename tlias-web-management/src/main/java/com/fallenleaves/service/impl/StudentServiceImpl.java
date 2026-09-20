package com.fallenleaves.service.impl;

import com.fallenleaves.mapper.StudentMapper;
import com.fallenleaves.pojo.Clazz;
import com.fallenleaves.pojo.PageResult;
import com.fallenleaves.pojo.Student;
import com.fallenleaves.pojo.StudentQueryParam;
import com.fallenleaves.service.StudentService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public PageResult<Student> page(StudentQueryParam studentQueryParam) {
        PageHelper.startPage(
                studentQueryParam.getPage(),
                studentQueryParam.getPageSize()
        );
        List<Student> studentList = studentMapper.list(studentQueryParam);
        Page<Student> p = (Page<Student>) studentList;
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Override
    public void deleteByIds(List<Integer> ids) {
        studentMapper.deleteByIds(ids);
    }

    @Override
    public void save(Student student) {
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        student.setViolationCount((short)0);
        studentMapper.insert(student);
    }

    @Override
    public Student findById(Integer id) {


        return studentMapper.findById(id);
    }

    @Override
    public void update(Student student) {
        student.setUpdateTime(LocalDateTime.now());
        studentMapper.update(student);


    }

    @Override
    public void violate(Integer id, Integer score) {
        Student s=studentMapper.findById(id);
        short currentCount = s.getViolationCount();
        s.setViolationCount((short) (currentCount + 1));
        s.setUpdateTime(LocalDateTime.now());
        short currentScore = s.getViolationScore();
        s.setViolationScore((short) (currentScore + score));

        studentMapper.update(s);
    }
}
