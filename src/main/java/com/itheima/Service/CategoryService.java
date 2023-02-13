package com.itheima.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Category;

public interface CategoryService extends IService<Category> {
    Boolean remove2(Long ids);

}
