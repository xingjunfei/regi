package com.itheima.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.Mapper.ShoppingCartMapper;
import com.itheima.Service.ShoppingCartService;
import com.itheima.entity.ShoppingCart;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceimpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
