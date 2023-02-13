package com.itheima.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 *
 * 全局异常处理器
 */


//这个注解的作用是，凡是加了括号内的控制器，如果出现了异常，会被下面的全局异常处理器所处理
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    //异常处理器
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception)
    {
        log.error(exception.getMessage());
        return R.error("添加失败");
    }


    @ExceptionHandler(MyException.class)
    public R<String> exceptionHandler2(MyException exception)
    {
        log.error(exception.getMessage());
        return R.error(exception.getMessage());
    }

}
