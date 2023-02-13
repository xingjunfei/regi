package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.Service.CategoryService;
import com.itheima.common.R;
import com.itheima.entity.Category;
import com.itheima.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryControl {

    @Resource
    private CategoryService categoryService;
    @PostMapping
    public R<String> add(@RequestBody Category category)
    {
        categoryService.save(category);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize)
    {


        Page<Category> categorypage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.orderByDesc(Category::getUpdateTime);
        categoryService.page(categorypage,lambdaQueryWrapper);
        return R.success(categorypage);
    }

    @DeleteMapping()
    public R<String> del(Long ids)
    {

        Boolean remove = categoryService.remove2(ids);

//        boolean remove = categoryService.removeById(ids);

        if(remove)
        {
            return R.success("删除成功");
        }
        return R.error("删除失败");

    }

    @PutMapping
    public R<String> update(@RequestBody Category category)
    {
        boolean b = categoryService.updateById(category);
        if(b)
        {
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }


    //获取分类列表
    @GetMapping("/list")
    public R<List<Category>> List(Category category)
    {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getType).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}
