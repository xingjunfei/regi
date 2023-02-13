package com.itheima.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.Mapper.DishMapper;
import com.itheima.Service.DishService;
import com.itheima.entity.Dish;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DishServiceimpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
