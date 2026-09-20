package com.fallenleaves.service.impl;


import com.fallenleaves.mapper.DeptMapper;
import com.fallenleaves.mapper.EmpExprMapper;
import com.fallenleaves.mapper.EmpLogMapper;
import com.fallenleaves.mapper.EmpMapper;
import com.fallenleaves.pojo.*;
import com.fallenleaves.service.EmpLogService;
import com.fallenleaves.service.EmpService;
import com.fallenleaves.utils.AliyunOSSOperator;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Service
public class EmpServiceImpl implements EmpService {



    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private EmpExprMapper empExprMapper;

    @Autowired
    private EmpLogService empLogService;
    @Autowired
    private EmpLogMapper empLogMapper;
    @Autowired
    private AliyunOSSOperator ossOperator;

    //新增员工
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Emp emp){
        try {
            //新增员工
            emp.setCreateTime(LocalDateTime.now());
            emp.setUpdateTime(LocalDateTime.now());
            empMapper.insert(emp);
            //2.保存员工工作经历
            List< EmpExpr > exprList=emp.getExprList();
            if (!CollectionUtils.isEmpty(exprList)){
                exprList.forEach(empExpr->{
                    empExpr.setEmpId(emp.getId());
                });
                empExprMapper.insertbatch(exprList);
            }
        } finally {
            //3记录操作日志
            EmpLog empLog=new EmpLog(null,LocalDateTime.now(),"新增员工："+emp);
            empLogService.insertLog(empLog);
        }


    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Integer> ids){

        //1.删除基本信息
        empMapper.deleteByIds(ids);
        //2.删除员工工作经历信息
        empExprMapper.deleteByEmpIds(ids);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Emp emp){
        //根据ID更改员工的基本信息
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);

        //2.根据员工id删除原先工作经历信息
        //2.1先删除原有的
        empExprMapper.deleteByEmpIds(Arrays.asList(emp.getId()));
        //2.2再添加这个员工新的工作经历
        List<EmpExpr> exprList=emp.getExprList();
        if (!CollectionUtils.isEmpty(exprList)){
            exprList.forEach(empExpr->
                empExpr.setEmpId(emp.getId()));
                empExprMapper.insertbatch(exprList);

        }
    }

    @Override
    public Emp getInfo(Integer id) {
        return empMapper.getById(id);
    }

    @Override
    public List<Emp> list() {
        List<Emp> empList=empMapper.findlist();
        return empList;
    }

    @Override
    public LoginInfo login(Emp emp) {
        //1.调用Mapper
        Emp e =empMapper.selectByUsernameAndPassword(emp);
        //2.判断是否存在员工
        if(e!=null){
            log.info("login:{}",e);
            return new LoginInfo(e.getId(),e.getUsername(),e.getName(),"");
        }

        return null;
    }

    @Override
    public PageResult<Emp> page(EmpQueryParam empQueryParam){
        PageHelper.startPage(empQueryParam.getPage(),empQueryParam.getPageSize());
        List<Emp> empList = empMapper.list(empQueryParam);
        Page<Emp> p = (Page<Emp>) empList;
        return new PageResult<Emp>(p.getTotal(),p.getResult());



//    public PageResult<Emp> page(Integer page,Integer pageSize,String name, Integer gender,
//                                LocalDate begin, LocalDate end){
//        PageHelper.startPage(page,pageSize);
//
//        List<Emp> empList = empMapper.list(name,gender,begin,end);
//
//        Page<Emp> p = (Page<Emp>) empList;
//
//        return new PageResult<Emp>(p.getTotal(),p.getResult());


        //原始的分页查询操作
//        Long total = empMapper.count();
//        Integer start = (page - 1) * pageSize;
//        List<Emp> rows = empMapper.list(start,pageSize);
//        return new PageResult<Emp>(total,rows);
    }





}
