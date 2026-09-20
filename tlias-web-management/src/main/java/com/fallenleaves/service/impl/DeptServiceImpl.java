package com.fallenleaves.service.impl;

import com.fallenleaves.mapper.DeptMapper;
import com.fallenleaves.pojo.Dept;
import com.fallenleaves.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public List<Dept> findAll() {
        return deptMapper.findAll();
    }

    @Override
    public void deleteById(Integer id) {deptMapper.deleteById(id);}

    @Override
    public void add(Dept dept) {
        //1.补全基础属性
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        //2.调用mapper
        deptMapper.insert(dept);
    }

    @Override
    public Dept getById(Integer id) {
        return deptMapper.getById(id);
    }

    @Override
    public void update(Dept dept) {
        //1.补全基础属性
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.update(dept);
    }


}
