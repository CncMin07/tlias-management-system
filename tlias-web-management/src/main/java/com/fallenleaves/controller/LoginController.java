package com.fallenleaves.controller;

import com.fallenleaves.pojo.Emp;
import com.fallenleaves.pojo.LoginInfo;
import com.fallenleaves.pojo.Result;
import com.fallenleaves.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoginController {

    @Autowired
    private EmpService empService;


    @PostMapping("/login")
    public Result Login(@RequestBody Emp emp){
        log.info("login:{}",emp);
        LoginInfo loginInfo =empService.login(emp);
        if(loginInfo!=null){
            return Result.success(loginInfo);
        }

        return Result.error("用户名或者密码错误");

    }

}
