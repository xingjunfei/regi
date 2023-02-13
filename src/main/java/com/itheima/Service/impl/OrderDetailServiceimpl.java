package com.itheima.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.Mapper.OrderDetailMapper;
import com.itheima.Service.OrderDetailService;
import com.itheima.entity.OrderDetail;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceimpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
