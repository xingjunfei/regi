package com.itheima.Service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.Mapper.AddressBookMapper;
import com.itheima.Service.AddressBookService;
import com.itheima.entity.AddressBook;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceimpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
