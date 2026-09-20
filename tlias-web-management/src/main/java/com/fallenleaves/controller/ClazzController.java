package com.fallenleaves.controller;


import com.fallenleaves.pojo.*;
import com.fallenleaves.service.ClazzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@Slf4j
@RequestMapping("/clazzs")
@RestController
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

    @GetMapping
    public Result page(ClazzQueryParam clazzQueryParam) {
        log.info("page：{}", clazzQueryParam);
        PageResult<Clazz> pageResult = clazzService.page(clazzQueryParam);
        return Result.success(pageResult);
    }

    @DeleteMapping("/{id}")
    public Result deleteClazz(@PathVariable Integer id) {
        log.info("删除班级id:{}", id);
        clazzService.deleteClazz(id);
        return Result.success();
    }

    @PostMapping
    public Result saveClazz(@RequestBody Clazz clazz){
        log.info("存入班级");
        clazzService.saveClazz(clazz);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getClazzById(@PathVariable Integer id){
        log.info("getClazzById:{}",id);
        Clazz clazz = clazzService.getClazzById(id);
        return Result.success(clazz);
    }

    @PutMapping
    public Result updateClazz(@RequestBody Clazz clazz){
        log.info("updateClazz:{}",clazz);
        clazzService.updateClazz(clazz);
        return Result.success();
    }

    @GetMapping("/list")
    public Result findAll(){
        List<Clazz> clazzList = clazzService.findAll();
        return Result.success(clazzList);
    }





}
