package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.Service.CategoryService;
import com.itheima.Service.SetmealDishService;
import com.itheima.Service.SetmealService;
import com.itheima.common.MyException;
import com.itheima.common.R;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Category;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDish;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealControl {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name)
    {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name!=null,Setmeal::getName,name);

        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        setmealService.page(setmealPage,lambdaQueryWrapper);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //复制数据，但有些数据不复制，我们自己设置
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> setmealList = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList = setmealDtoPage.getRecords();
        ArrayList<SetmealDto> list = new ArrayList<>();

        for (Setmeal setmeal : setmealList) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);
            //获得当前套餐所属的分类名
            Long categoryId = setmeal.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            //把这个分类名设置给 setmealDto的属性
            setmealDto.setCategoryName(categoryName);

            list.add(setmealDto);
        }
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    @PostMapping
    @Transactional
    public R<String> save(@RequestBody  SetmealDto setmealDto)
    {
        System.out.println(setmealDto.getId());
        boolean save = setmealService.save(setmealDto);
        if(!save)
        {
            return R.error("保存失败");
        }

        //只有setmeal表保存完后，我们才能得到setmealid,即后续操作才能进行下去
        //现在要获得setmealid，因为下个表保存时，要使用到这个属性
        Long id = setmealDto.getId();


        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishList) {

            //这里就用到了setmealid
            setmealDish.setSetmealId(id);

            boolean save1 = setmealDishService.save(setmealDish);
            if(!save1)
            {
                return R.error("保存失败");
            }
        }
        return R.success("保存成功");
    }

    //修改套餐，回显
    @GetMapping("/{id}")
    public R<SetmealDto> update(@PathVariable Long id)
    {
        SetmealDto setmealDto = new SetmealDto();
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //这里查的是SetmealId
        lambdaQueryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);
        Setmeal setmeal = setmealService.getById(id);
        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);

        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(list);
        return R.success(setmealDto);
    }

    //修改套餐，保存
    @PutMapping
    public R<String> update2(@RequestBody SetmealDto setmealDto)
    {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto,setmeal);
        boolean save = setmealService.updateById(setmeal);
        if(!save)
        {
            return R.error("修改失败");
        }

        Long id = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);
        boolean remove = setmealDishService.remove(lambdaQueryWrapper);

        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();

        for (SetmealDish setmealDish : setmealDishList) {

            setmealDish.setSetmealId(id);
            setmealDishService.save(setmealDish);
        }
        return R.success("修改成功");
    }

    @DeleteMapping
    @Transactional
    public R<String> del(String ids)
    {
        String[] split = ids.split(",");
        if(split==null)
        {
            return R.error("删除失败");
        }

        //删除前，要判断套餐状态，只有是停售状态才可以删除
        for (String id : split) {
            long l = Long.parseLong(id);
            Setmeal setmeal = setmealService.getById(l);
            Integer status = setmeal.getStatus();

            if(status==1)
            {
                throw new MyException("套餐正在售卖中，不可被删除");
            }
            boolean remove = setmealService.removeById(l);

            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);
            boolean remove1 = setmealDishService.remove(lambdaQueryWrapper);
            if(!(remove&&remove1))
            {
                return R.error("删除失败");
            }

        }
        return R.success("删除成功");
    }

    //修改套餐状态
    @PostMapping("/status/{stas}")
    public R status(@PathVariable Integer stas,@RequestParam List<Long> ids)
    {
        LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(ids.size()>0,Setmeal::getId,ids);
        lambdaUpdateWrapper.set(Setmeal::getStatus,stas);
        boolean update = setmealService.update(lambdaUpdateWrapper);
        if(!update)
        {
            return R.error("修改状态失败");
        }

        return R.success("修改状态成功");
    }


    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal)
    {
        LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaUpdateWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> setmealList = setmealService.list(lambdaUpdateWrapper);
        return R.success(setmealList);
    }

}
