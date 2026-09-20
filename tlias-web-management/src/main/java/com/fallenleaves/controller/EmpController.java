package com.fallenleaves.controller;


import com.fallenleaves.pojo.Emp;
import com.fallenleaves.pojo.EmpQueryParam;
import com.fallenleaves.pojo.PageResult;
import com.fallenleaves.pojo.Result;
import com.fallenleaves.service.DeptService;
import com.fallenleaves.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.fallenleaves.pojo.Result.success;

@Slf4j
@RequestMapping("/emps")
@RestController
public class EmpController {

    @Autowired
    private EmpService empService;


    //分页查询员工操作
    @GetMapping
    public Result page(EmpQueryParam empQueryParam){
        log.info("page：{}",empQueryParam);
        PageResult<Emp> pageResult = empService.page(empQueryParam);
        return Result.success(pageResult);
    }

    //新增员工操作
    @PostMapping
    public Result save(@RequestBody Emp emp){
        log.info("save emp:{}",emp);
        empService.save(emp);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids){
        log.info("delete emp:{}",ids);
        empService.delete(ids);
        return Result.success();
    }

    //修改员工
    @PutMapping
    public Result update(@RequestBody Emp emp){

        log.info("update emp:{}",emp);
        empService.update(emp);
        return Result.success();
    }
//    根据id查询员工信息
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        log.info("getInfoById:{}",id);
        Emp emp = empService.getInfo(id);
        return Result.success(emp);
    }

    @GetMapping("/list")
    public Result list(){
        log.info("查询所有的员工数据");
        List<Emp> empList = empService.list();
        return Result.success(empList);
    }




//    @DeleteMapping
//    public Result delete(Integer[] ids){
//        log.info("delete emp:{}", Arrays.toString(ids));
//        return Result.success();
//
//    }



    //分页查询
//    @GetMapping
//    public Result page(@RequestParam(defaultValue = "1") Integer page,
//                       @RequestParam(defaultValue = "10") Integer pageSize,
//                       String name, Integer gender,
//                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
//                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
//        log.info("page:{},pageSize:{},name:{},gender:{},begin:{},end:{}",page,pageSize,name,gender,begin,end);
//        PageResult<Emp> pageResult = empService.page(page,pageSize,name,gender,begin,end);
//        return Result.success(pageResult);
//
//    }







}
