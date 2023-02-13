package com.itheima.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.Mapper.CategoryMapper;
import com.itheima.Service.CategoryService;
import com.itheima.Service.DishService;
import com.itheima.Service.SetmealService;
import com.itheima.common.MyException;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.Setmeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceimpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    /**
     * 自定义删除，如果删除项已被其他关联，则不能删除
     */
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public Boolean remove2(Long ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count = dishService.count(dishLambdaQueryWrapper);
        if(count>0)
        {
            //不能删，并且抛一个自定义异常
            throw new MyException("该菜型已被其他餐品使用，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if(count1>0)
        {
            //不能删，并且抛一个自定义异常
            throw new MyException("该套餐已被其他餐品使用，不能删除");
        }

        //如果没有关联任何，则正常删除
        boolean b = super.removeById(ids);
        if(b)
        {
            return true;
        }
        return false;

    }
}
