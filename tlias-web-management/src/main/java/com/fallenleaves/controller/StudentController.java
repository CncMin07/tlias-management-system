package com.fallenleaves.controller;

import com.fallenleaves.pojo.*;
import com.fallenleaves.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/students")
@RestController
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public Result Page(StudentQueryParam studentQueryParam) {
        log.info("page：{}",studentQueryParam);
        PageResult<Student> pageResult = studentService.page(studentQueryParam);
        return Result.success(pageResult);
    }

    @DeleteMapping("/{ids}")
    public Result deleteByIds(@PathVariable List<Integer> ids) {
        log.info("delete student:{}",ids);
        studentService.deleteByIds(ids);
        return Result.success();
    }

    @PostMapping
    public Result save(@RequestBody Student student) {
        log.info("save student:{}",student);
        studentService.save(student);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        log.info("findById:{}",id);
        Student s = studentService.findById(id);
        return Result.success(s);
    }

    @PutMapping
    public Result update(@RequestBody Student student) {
        log.info("update student:{}",student);
        studentService.update(student);
        return Result.success();
    }

    @PutMapping("/violation/{id}/{score}")
    public Result violate(@PathVariable Integer id, @PathVariable Integer score) {
        log.info("violate student:{}",id);
        studentService.violate(id,score);
        return Result.success();
    }

}
