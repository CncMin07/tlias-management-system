package com.fallenleaves.exception;

import com.fallenleaves.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result handlerException(Exception e) {
        log.error("程序出错啦~",e);
        return Result.error("出错啦，请联系管理员");
    }

    @ExceptionHandler
    public Result handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("程序出错啦~", e);
        String message = e.getMessage();
        int i = message.indexOf("Duplicate entry");
        String errMsg = message.substring(i);
        String[] arr = errMsg.split(" ");
        return Result.error(arr[2] + " 已存在");
    }
}
