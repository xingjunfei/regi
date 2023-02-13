package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.Service.EmployeeService;
import com.itheima.common.R;
import com.itheima.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;


import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeControl {


    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //获取username,在数据库查询是否存在
        String username = employee.getUsername();
        //不存在，直接退出
        Employee employee1 = employeeService.existsusername(username);
        if (employee1 == null) {
            return R.error("抱歉，该用户不存在");
        }

        //存在，再获取密码，进行MD5加密后，然后根据username查询密码是否和数据库一致
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println(password);
        //不一致，退出
        Employee employee2 = employeeService.existspassword(username,password);
        if (employee2 == null) {
            return R.error("抱歉,用户名或密码错误");
        }

        //一致，在查状态是否被冻结
        Integer status = employee2.getStatus();
        System.out.println("状态是"+status);
        //冻结，退出
        if (status == 0) {
            return R.error("账号已被冻结");
        }
        //未冻结，登录成功
        HttpSession session = request.getSession();
        session.setAttribute("employee",employee2.getId());
        return R.success(employee2);
    }


    @PostMapping("/logout")
    public R<String> logout(HttpSession session,HttpServletRequest request)
    {
        session.removeAttribute("employee");
        return R.success("退出成功");
    }


    @PostMapping
    public R<String> adduser(HttpServletRequest request,@RequestBody Employee employee)
    {
        log.info("新增员工信息: {}",employee.toString());

        if(employee==null)
        {
            return R.error("添加失败");
        }

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

//        Long id = (Long)request.getSession().getAttribute("employee");
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }


    //分页
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name)
    {

        log.info("分页信息: page {},pageSize {},name {}",page,pageSize,name);
        Page<Employee> employeePage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //升序排列
        lambdaQueryWrapper.orderByAsc(Employee::getUpdateTime);
        employeeService.page(employeePage,lambdaQueryWrapper);

        return R.success(employeePage);
    }


    //修改
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee)
    {
        log.info("修改员工的信息:{}",employee.toString());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long employeeID =(Long)request.getSession().getAttribute("employee");
//        employee.setUpdateUser(employeeID);

        boolean update = employeeService.updateById(employee);
        if(update)
        {
            return R.success("修改成功");
        }
        return R.error("修改失败");

    }


    //查询员工信息，并回显
    @GetMapping("/{id}")
    public R<Employee> getemployee(@PathVariable Long id)
    {
        Employee employee = employeeService.getById(id);
        if(employee==null)
        {
            return R.error("员工不存在");
        }
        return R.success(employee);
    }


}
