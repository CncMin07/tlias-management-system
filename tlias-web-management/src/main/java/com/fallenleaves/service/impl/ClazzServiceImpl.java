package com.fallenleaves.service.impl;

import com.fallenleaves.mapper.ClazzMapper;
import com.fallenleaves.pojo.Clazz;
import com.fallenleaves.pojo.ClazzQueryParam;
import com.fallenleaves.pojo.PageResult;
import com.fallenleaves.service.ClazzService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClazzServiceImpl implements ClazzService {

    @Autowired
    private ClazzMapper clazzMapper;

    @Override
    public PageResult<Clazz> page(ClazzQueryParam clazzQueryParam) {

        // 1️⃣ 开启分页
        PageHelper.startPage(
                clazzQueryParam.getPage(),
                clazzQueryParam.getPageSize()
        );

        // 2️⃣ 查询数据（此时 status = null）
        List<Clazz> clazzList = clazzMapper.list(clazzQueryParam);

        // 3️⃣ 计算并设置状态 ✅ 关键一步
        LocalDate now = LocalDate.now();
        for (Clazz clazz : clazzList) {
            clazz.setStatus(judgeStatus(now, clazz));
        }

        // 4️⃣ 强转 Page（本质还是原来的 List）
        Page<Clazz> p = (Page<Clazz>) clazzList;

        // 5️⃣ 封装返回
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Override
    public void deleteClazz(Integer id) {
        clazzMapper.deleteById(id);
    }

    @Override
    public void saveClazz(Clazz clazz) {
        clazzMapper.insertClazz(clazz);

    }

    public Clazz getClazzById(Integer id) {
        return clazzMapper.getClazzById(id);
    }

    @Override
    public void updateClazz(Clazz clazz) {
        clazz.setUpdateTime(LocalDateTime.now());
        clazzMapper.updateClazz(clazz);
    }

    @Override
    public List<Clazz> findAll() {
        return clazzMapper.findAll();
    }


    /** ✅ 状态判断方法 */
    private String judgeStatus(LocalDate now, Clazz clazz) {
        if (now.isAfter(clazz.getEndDate())) {
            return "已结课";
        }
        if (now.isBefore(clazz.getBeginDate())) {
            return "未开班";
        }
        return "在读中";
    }
}



//package com.fallenleaves.service.impl;
//
//import com.fallenleaves.mapper.ClazzMapper;
//import com.fallenleaves.pojo.*;
//import com.fallenleaves.service.ClazzService;
//import com.github.pagehelper.Page;
//import com.github.pagehelper.PageHelper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ClazzServiceImpl implements ClazzService {
//
//    @Autowired
//    private ClazzMapper clazzMapper;
//
//    @Override
//    public PageResult<Clazz> page(ClazzQueryParam clazzQueryParam){
//        PageHelper.startPage(clazzQueryParam.getPage(),clazzQueryParam.getPageSize());
//        List<Clazz> clazzList = clazzMapper.list(clazzQueryParam);
//        Page<Clazz> p = (Page<Clazz>) clazzList;
//        return new PageResult<Clazz>(p.getTotal(),p.getResult());
//    }
//
//}
