package com.itheima.common;

public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setID(Long id)
    {
        threadLocal.set(id);
    }


    public static Long getID()
    {
        Long id = threadLocal.get();
        return id;
    }
}
