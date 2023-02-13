package com.itheima.Service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.Mapper.EmployeeMapper;
import com.itheima.Service.EmployeeService;
import com.itheima.entity.Employee;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EmployeeServiceimpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;

    //查询username是否存在
    @Override
    public Employee existsusername(String username) {

        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        Employee employee = employeeMapper.selectOne(queryWrapper);
        return employee;
    }


    //查询密码是否正确
    @Override
    public Employee existspassword(String username,String password) {

        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("password",password).eq("username",username);
        Employee employee = employeeMapper.selectOne(queryWrapper);
        return employee;
    }


}
