package com.fallenleaves.mapper;


import com.fallenleaves.pojo.Dept;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DeptMapper {

    //查询所有的部门数据
    @Select("select id, name, create_time createTime, update_time updateTime from dept order by update_time desc")
    List<Dept> findAll();


    @Delete("delete from dept where id = #{id}")
    void deleteById(Integer id);


    @Insert("INSERT INTO dept (name,create_time,update_time) values(#{name},#{createTime},#{updateTime}) ")
    void insert(Dept dept);

    @Select("select id, name, create_time createTime, update_time updateTime from dept where id=#{id}")
    Dept getById(Integer id);

    @Update("update dept set name=#{name},update_time=#{updateTime} where id=#{id}")
    void update(Dept dept);


}
