package com.fallenleaves.controller;

import com.fallenleaves.pojo.ClazzOption;
import com.fallenleaves.pojo.DegreeOption;
import com.fallenleaves.pojo.JobOption;
import com.fallenleaves.pojo.Result;
import com.fallenleaves.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@Slf4j
@RequestMapping("/report")
@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/empJobData")
    public Result getEmpJobData(){
        log.info("统计员工职位人数");
        JobOption jobOption = reportService.getEmpJobData();
        return Result.success(jobOption);
    }

    //统计员工性别信息
    @GetMapping("/empGenderData")
    public Result getEmpGenderData(){
        log.info("统计员工职位性别");
        List<Map<String,Object>> genderlist = reportService.getEmpGenderData();
        return Result.success(genderlist);

    }

    @GetMapping("/studentDegreeData")
    public Result getStudentJobData(){
        log.info("统计学生学位人数");
        List list = reportService.getStudentDegreeData();
        return Result.success(list);
    }


    @GetMapping("/studentCountData")
    public Result countStudentData(){
        log.info("统计各班级学生人数");
        ClazzOption clazzOption = reportService.countStudentData();
        return Result.success(clazzOption);
    }


    }








