package com.itheima.common;

/**
 *
 * 自定义异常
 */
public class MyException extends RuntimeException{

    public MyException(String msg)
    {
        super(msg);
    }
}
