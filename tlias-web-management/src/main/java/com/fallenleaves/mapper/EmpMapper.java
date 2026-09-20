package com.fallenleaves.mapper;

import com.fallenleaves.pojo.Dept;
import com.fallenleaves.pojo.Emp;
import com.fallenleaves.pojo.EmpQueryParam;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface EmpMapper {

    //@Select("select e.* ,d.name deptName from emp e left join dept d on e.dept_id=d.id order by e.update_time desc" )
    public List<Emp>list(EmpQueryParam empQueryParam);



    @Options(useGeneratedKeys = true,keyProperty = "id")//获取到生成的主键mybatis提供
    @Insert("insert into emp(username, name, gender, phone, job, salary, image, entry_date, dept_id, create_time, update_time)" +
            "values(#{username},#{name},#{gender},#{phone},#{job},#{salary},#{image},#{entryDate},#{deptId},#{createTime},#{updateTime})")
    void insert(Emp emp);



    void deleteByIds(List<Integer> ids);

//    @Update("")
//    void update(Emp emp);

    //根据id查询员工信息以及员工工作经历信息
    Emp getById(Integer id);


    //根据id更新员工基本信息
    void updateById(Emp emp);

    @MapKey("POS")
    List<Map<String,Object>> countEmpJobData();

    @MapKey("name")
    List<Map<String, Object>> countEmpGenderData();

    @Select("select * from emp")
    List<Emp> findlist();

    @Select("select id,username,name from emp where username=#{username} and password=#{password}")
    Emp selectByUsernameAndPassword(Emp emp);


    //原先的查询方法
//    //查询总记录数
//    @Select("select count(*) from emp e left join dept d on e.dept_id=d.id;")
//    public Long count();
//
//    @Select("select e.* ,d.name deptName from emp e left join dept d on e.dept_id=d.id order by e.update_time desc limit #{start} , #{pageSize}" )
//    public List<Emp>list(Integer start,Integer pageSize);

}
