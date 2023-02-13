//package com.itheima.filter;
//
//import com.alibaba.fastjson.JSON;
//import com.itheima.common.R;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.web.servlet.ServletComponentScan;
//import org.springframework.util.AntPathMatcher;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//
//
//@WebFilter(filterName = "logincheckFilter",urlPatterns = "/*")
//@Slf4j
//public class logincheckFilter implements Filter {
//
//    //路径匹配器
//    public static final AntPathMatcher PATH_MATCHERMatcher=new AntPathMatcher();
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//
//        HttpServletRequest request =  (HttpServletRequest) servletRequest;
//        HttpServletResponse response=(HttpServletResponse) servletResponse;
//        log.info("拦截到请求: {}",request.getRequestURI());
//        String requestURI = request.getRequestURI();
//        String[] urls=new String[]{
//                        "/employee/login",
//                        "/employee/logout",
//                        "/backend/**",
//                        "/front/**"
//                };
//
//        Boolean check = check(urls, requestURI);
//        if(check)
//        {
//            filterChain.doFilter(request,response);
//            return;
//        }
//
//
//        HttpSession session = request.getSession();
//        Object employee = session.getAttribute("employee");
//        if(employee!=null)
//        {
//            filterChain.doFilter(request,response);
//            return;
//        }
//
//        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
//        return;
//    }
//
//
//    public Boolean check(String[] urls,String requestURI)
//    {
//        for (String url : urls) {
//            boolean match = PATH_MATCHERMatcher.match(url, requestURI);
//            if(match)
//            {
//                return true;
//            }
//        }
//        return false;
//    }
//}
