package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.Service.CategoryService;
import com.itheima.Service.DishFlavorService;
import com.itheima.Service.DishService;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishControl {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping
    @Transactional
    public R<String> save(@RequestBody DishDto dishDto)
    {
        //redis优化
        String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();

        boolean save = dishService.save(dishDto);
//        Dish dish=new Dish();
//        if(save)
//        {
//            LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.eq(Dish::getName,dishDto.getName());
//            dish = dishService.getOne(lambdaQueryWrapper);
//        }

        List<DishFlavor> flavorList = dishDto.getFlavors();


        for (DishFlavor dishFlavor : flavorList) {
            dishFlavor.setDishId(dishDto.getId());
            dishFlavorService.save(dishFlavor);
        }

        Boolean delete = redisTemplate.delete(key);
        log.info("执行了添加操作，要删除缓存,执行结果: {}",delete);
        return R.success("保存成功");
    }


    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name)
    {
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name!=null,Dish::getName,name);
        Page<Dish> dishPage = new Page<>(page,pageSize);
        dishService.page(dishPage,lambdaQueryWrapper);

        Page<DishDto> dishDtoPage = new Page<>();
        List<Dish> dishList = dishPage.getRecords();
        List<DishDto> dishDtoList = dishDtoPage.getRecords();

        //复制所有属性，但除了"records"属性，这个我们自己设置
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        ArrayList<DishDto> list = new ArrayList<>();

        for (Dish dish : dishList) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);

            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null)
            {
                String categoryName = category.getName();

                //一切都是为了这个
                dishDto.setCategoryName(categoryName);

            }

             list.add(dishDto);

        }
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    //修改，回显数据
    @GetMapping("/{id}")
    public R<DishDto> updateAndreturn(@PathVariable Long id)
    {
        DishDto dishDto = new DishDto();
        Dish dish = dishService.getById(id);
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper =
                new LambdaQueryWrapper<DishFlavor>().eq(id != null, DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(list);
        return R.success(dishDto);
    }


    //修改，保存
    @PutMapping
    public R<String> updateAndSave(@RequestBody DishDto dishDto)
    {
        //redis优化
        String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();


        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto,dish);
        boolean updateById = dishService.updateById(dish);
        if(!updateById)
        {
            return R.error("保存失败");
        }


        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dishDto.getId()!=null,DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);


        List<DishFlavor> flavorList = dishDto.getFlavors();
        for (DishFlavor flavor : flavorList) {
            flavor.setDishId(dishDto.getId());
            dishFlavorService.save(flavor);
        }

        //执行了修改操作，要删除缓存，目的是保持数据库和缓存一致
        Boolean delete = redisTemplate.delete(key);
        log.info("执行了修改操作，要删除缓存,执行结果: {}",delete);
        return R.success("保存成功");
    }


//    @GetMapping("/list")
//    public R<List> list(Long categoryId,String name)
//    {
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(categoryId!=null,Dish::getCategoryId,categoryId);
//        lambdaQueryWrapper.like(name!=null,Dish::getName,name);
//        List<Dish> list = dishService.list(lambdaQueryWrapper);
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish2,String name)
    {
        List<DishDto> list= null;
        Long categoryId = dish2.getCategoryId();

        //拼接一个redis的 key
        String key="dish_"+categoryId+"_"+dish2.getStatus();

        //先去redis中查
       list= ( List<DishDto>)redisTemplate.opsForValue().get(key);

        //如果查得到，直接返回

        if(list!=null)
        {
            return R.success(list);
        }
        //查不到，去数据库查


        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(categoryId!=null,Dish::getCategoryId,categoryId);
        lambdaQueryWrapper.like(name!=null,Dish::getName,name);
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        List<Dish> dishList = dishService.list(lambdaQueryWrapper);
        ArrayList<DishDto> dishDtoList = new ArrayList<>();

        //主要是为了使用DishDto中的口味DishFlavor数据


        //大致逻辑就是，先获得指定条件的菜品集合，但菜品实体没有口味属性，所以要遍历集合
        //一个一个的把菜品数据赋值到DishDto，再根据当前菜品的id,去口味表查它所对应的口味
        //然后DishDto设置这些口味，并将DishDto加入到DishDto集合返回
        for (Dish dish : dishList) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);

            Long dishId = dish.getId();
            LambdaUpdateWrapper<DishFlavor> lambdaUpdateWrapper2 = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper2.eq(dishId!=null,DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaUpdateWrapper2);

            dishDto.setFlavors(dishFlavorList);

            dishDtoList.add(dishDto);
        }
        //最后还要将查到的数据写入redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }







}
