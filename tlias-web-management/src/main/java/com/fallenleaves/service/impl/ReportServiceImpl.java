package com.fallenleaves.service.impl;

import com.fallenleaves.mapper.ClazzMapper;
import com.fallenleaves.mapper.EmpMapper;
import com.fallenleaves.mapper.StudentMapper;
import com.fallenleaves.pojo.Clazz;
import com.fallenleaves.pojo.ClazzOption;
import com.fallenleaves.pojo.DegreeOption;
import com.fallenleaves.pojo.JobOption;
import com.fallenleaves.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {


    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private ClazzMapper clazzMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public JobOption getEmpJobData(){
        //1.调用mapper接口获取统计数据
        List<Map<String,Object>> list = empMapper.countEmpJobData();

        //2.组装结果并返回
        List<Object> jobList = list.stream().map(dataMap -> dataMap.get("pos")).toList();
        List<Object> dataList = list.stream().map(dataMap -> dataMap.get("num")).toList();
        return new JobOption(jobList, dataList);
    }

    @Override
    public List<Map<String, Object>> getEmpGenderData() {
        return empMapper.countEmpGenderData();
    }

    @Override
    public List<Map<String, Object>> getStudentDegreeData() {
        //1.调用mapper接口获取统计数据
        List<Map<String,Object>> list = studentMapper.countStudentDegreeData();
        return list;
    }

    @Override
    public ClazzOption countStudentData() {

        //1.调用mapper接口获取统计数据
        List<Map<String,Object>> list = studentMapper.countStudentData();

        //2.组装结果并返回
        List<Object> clazzList = list.stream().map(dataMap -> dataMap.get("clazzName")).toList();
        List<Object> dataList = list.stream().map(dataMap -> dataMap.get("studentCount")).toList();
        return new ClazzOption(clazzList, dataList);
    }




}
