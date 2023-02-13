package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.Service.ShoppingCartService;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.entity.ShoppingCart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartControl {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @Transactional
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart)
    {
        //获得当前用户id
        Long id = BaseContext.getID();
        shoppingCart.setUserId(id);
        //判断当前时套餐还是菜品，并根据相应id和用户id联合查询是否已经添加过该菜品
        Long dishId = shoppingCart.getDishId();
        LambdaUpdateWrapper<ShoppingCart> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        lambdaUpdateWrapper.eq(shoppingCart.getUserId()!=null,ShoppingCart::getUserId,shoppingCart.getUserId());

        if(dishId!=null)
        {
            //说明当前是菜品
            lambdaUpdateWrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());

        }else
            {
                //说明是套餐
                lambdaUpdateWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            }
        ShoppingCart one = shoppingCartService.getOne(lambdaUpdateWrapper);

        //如果添加过，则将数量+1后，再保存
        if(one!=null)
        {
            Integer number = one.getNumber();
            one.setNumber(number+1);
            shoppingCartService.updateById(one);
        }else
            {
                //如果没添加过，则将数量设置为1，添加
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCartService.save(shoppingCart);
                one=shoppingCart;
            }

        return R.success(one);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list()
    {
        LambdaUpdateWrapper<ShoppingCart> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(ShoppingCart::getUserId,BaseContext.getID());
        List<ShoppingCart> list = shoppingCartService.list(lambdaUpdateWrapper);
        return R.success(list);
    }


    @DeleteMapping("/clean")
    public R<String> del()
    {
        Long id = BaseContext.getID();
        LambdaUpdateWrapper<ShoppingCart> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(id!=null,ShoppingCart::getUserId,id);
        boolean remove = shoppingCartService.remove(lambdaUpdateWrapper);
        if(remove)
        {
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }


}
