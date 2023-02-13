package com.itheima.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.Mapper.SetmealMapper;
import com.itheima.Service.SetmealService;
import com.itheima.entity.Setmeal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealServiceimpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {
}
