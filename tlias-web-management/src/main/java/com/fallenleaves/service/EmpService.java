package com.fallenleaves.service;

import com.fallenleaves.pojo.Emp;
import com.fallenleaves.pojo.EmpQueryParam;
import com.fallenleaves.pojo.LoginInfo;
import com.fallenleaves.pojo.PageResult;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public interface EmpService {



    PageResult<Emp> page(EmpQueryParam empQueryParam);

    void save(Emp emp);

    void delete(List<Integer> ids);

    void update(Emp emp);

    //根据id查询员工信息
    Emp getInfo(Integer id);

    List<Emp> list();

    LoginInfo login(Emp emp);


//    PageResult<Emp> page(Integer page, Integer pageSize,String name, Integer gender,
//                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
//                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);
}
