package com.itheima.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.Mapper.OrdersMapper;
import com.itheima.Service.OrdersService;
import com.itheima.entity.Orders;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceimpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
}
