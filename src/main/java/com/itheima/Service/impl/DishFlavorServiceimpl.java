package com.itheima.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.Mapper.DishFlavorMapper;
import com.itheima.Service.DishFlavorService;
import com.itheima.entity.DishFlavor;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceimpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
