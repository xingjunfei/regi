package com.itheima.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Employee;

public interface EmployeeService extends IService<Employee> {
    Employee existsusername(String username);

    Employee existspassword(String username,String password);



}
