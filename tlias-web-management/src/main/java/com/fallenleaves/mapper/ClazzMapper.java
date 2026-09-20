package com.fallenleaves.mapper;

import com.fallenleaves.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClazzMapper {
    //查询总记录数

    public List<Clazz>list(ClazzQueryParam clazzQueryParam);

    void deleteById(Integer id);

    void insertClazz(Clazz clazz);

    Clazz getClazzById(Integer id);

    void updateClazz(Clazz clazz);

    @Select("select * from clazz")
    List<Clazz> findAll();


}