package com.fallenleaves.mapper;


import com.fallenleaves.pojo.Student;
import com.fallenleaves.pojo.StudentQueryParam;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {

    //    @Select("select * from student")
    List<Student> list(StudentQueryParam studentQueryParam);


    void deleteByIds(List<Integer> ids);


    @Insert("insert into student(name,no,gender,phone,id_card,is_college,address,degree,graduation_date,clazz_id,update_time,create_time,violation_count)" +
            "value(#{name},#{no},#{gender},#{phone},#{idCard},#{isCollege},#{address},#{degree},#{graduationDate},#{clazzId},#{updateTime},#{createTime},#{violationCount})")
    void insert(Student student);

    @Select("SELECT s.* , c.name as clazzName from student s left join clazz c on s.clazz_id=c.id where s.id=#{id} ")
    Student findById(Integer id);


    void update(Student student);

    @MapKey("name")
    List<Map<String, Object>> countStudentDegreeData();


    @MapKey("clazz")
    List<Map<String, Object>> countStudentData();

}