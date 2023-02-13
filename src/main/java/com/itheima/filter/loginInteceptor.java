package com.itheima.filter;


import com.alibaba.fastjson.JSON;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class loginInteceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object employeeID = session.getAttribute("employee");
        Object userID = session.getAttribute("user");
        if(employeeID!=null)
        {
            BaseContext.setID((Long) employeeID);
            return true;
        }

        if(userID!=null)
        {
            BaseContext.setID((Long) userID);
            return true;
        }


       log.info("拦截到请求: {}",request.getRequestURI());
        response.sendRedirect(request.getContextPath()+"/backend/page/login/login.html");
        return false;
    }
}
