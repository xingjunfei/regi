package com.itheima.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.Mapper.UserMapper;
import com.itheima.Service.UserService;
import com.itheima.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceimpl extends ServiceImpl<UserMapper, User> implements UserService {

}
