package com.fallenleaves.service;

import com.fallenleaves.pojo.Dept;
import com.fallenleaves.pojo.Emp;
import com.fallenleaves.pojo.PageResult;

import java.util.List;

public interface DeptService {

    //查询所有部门数据
    List<Dept> findAll();

    void deleteById(Integer id);

    void add(Dept dept);

    Dept getById(Integer id);

    void update(Dept dept);


}
