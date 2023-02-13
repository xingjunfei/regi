package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.itheima.Service.*;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/order")
public class OrdersControl {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;



    @PostMapping("/submit")
    @Transactional
    public R<String> submit(@RequestBody Orders orders)
    {
        //获得用户id
        Long id = BaseContext.getID();
        //根据id查询所对应的购物车里的物品
        LambdaUpdateWrapper<ShoppingCart> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(id!=null,ShoppingCart::getUserId,id);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(lambdaUpdateWrapper);

        //如果查不到，报错
        if(shoppingCartList==null||shoppingCartList.size()<1)
        {
            return R.error("购物车为空，不能支付");
        }

        //提前做一些准备数据
        //订单中，有两个人，一个是创建此订单的，还一个是收货人
        //这个是创建订单user,收货人在addressBook获得
        User user = userService.getById(id);

        //地址信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());

        if(addressBook==null)
        {
            return R.error("地址信息为空，不能下单");
        }

        long orderId = IdWorker.getId();//订单号


        //计算一下总金额amount,同时设置orderDetail
        //这个AtomicInteger保证线程安全，避免多线程时计错误
        AtomicInteger totalAmount = new AtomicInteger(0);

        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setName(shoppingCart.getName());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setOrderId(orderId);
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setAmount(shoppingCart.getAmount());


            //addAndGet,加等，+=
            totalAmount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());
            orderDetailList.add(orderDetail);
        }




        //保存到订单表，一条数据
        orders.setNumber(String.valueOf(orderId));
        //已支付，待派送
        orders.setStatus(2);
        orders.setUserId(id);
//        orders.setAddressBookId(orders.getAddressBookId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setPayMethod(1);
        orders.setAmount(new BigDecimal(totalAmount.get()));
//        orders.setRemark();
        orders.setPhone(addressBook.getPhone());

        orders.setUserName(user.getName());
        //设置收货地址
        orders.setConsignee(addressBook.getConsignee());
                orders.setAddress(
                        (addressBook.getProvinceName()==null ? "":addressBook.getProvinceName())
                                + (addressBook.getCityName()==null ? "":addressBook.getCityName())
                                + (addressBook.getDistrictName()==null ?"":addressBook.getDistrictName())
                                + (addressBook.getDetail()==null ?"":addressBook.getDetail())
                );
        ordersService.save(orders);


    //保存到订单明细表，可能多条
    orderDetailService.saveBatch(orderDetailList);


        //清空购物车
        shoppingCartService.remove(lambdaUpdateWrapper);
        return R.success("支付成功");
    }

}
