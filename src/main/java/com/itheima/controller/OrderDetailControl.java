package com.itheima.controller;

import com.itheima.Service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orderDetail")
public class OrderDetailControl {
    @Autowired
    private OrderDetailService orderDetailService;

}
