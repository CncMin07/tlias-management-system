package com.fallenleaves.controller;


import com.fallenleaves.pojo.Dept;
import com.fallenleaves.pojo.Result;
import com.fallenleaves.service.DeptService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/depts")
@RestController
public class DeptController {

    @Autowired
    private DeptService deptService;

//    @RequestMapping(value = "/depts",method = RequestMethod.GET)
    @GetMapping
    public Result list(){
        System.out.println("查询全部部门数据");
        List<Dept>deptList = deptService.findAll();
        return Result.success(deptList);
    }


//    删除部门方式一httpservletrequest
//    @DeleteMapping("/depts")
//    public Result delete(HttpServletRequest request){
//        String idStr = request.getParameter("id");
//        int id = Integer.parseInt(idStr);
//        System.out.println("根据ID删除部门：" + id );
//        return Result.success();
//    }
    //删除部门方式2httpservletrequest
    //注意事项：一旦声明了@RequestParam请求时必须传递参数否则就会报错，如果不用必须的话就是把required改成false就行
//    @DeleteMapping("/depts")
//    public Result delete(@RequestParam(value = "id",required = false) Integer deptId){
//
//        System.out.println("根据ID删除部门：" + deptId );
//        return Result.success();}
    //方式三：前端传递过来的参数名和服务器端方法形参命一致可以省略requestparam
    @DeleteMapping
    public Result delete(Integer id){
        System.out.println("根据ID删除部门：" + id );
        deptService.deleteById(id);
        return Result.success();}

    @PostMapping
    public Result add(@RequestBody Dept dept){
        deptService.add(dept);
        System.out.println("新增部门：" + dept);
        return Result.success();
    }


    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        System.out.println("根据id查询部门数据" + id);
        Dept dept=deptService.getById(id);
        return Result.success(dept);
    }

    //修改部门
    @PutMapping
    public Result update(@RequestBody Dept dept){

        System.out.println("修改部门" + dept);
        deptService.update(dept);
        return Result.success();

    }



}
