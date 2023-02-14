package com.itheima.controller;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.Service.UserService;
import com.itheima.common.R;
import com.itheima.common.ValidateCodeUtils;
import com.itheima.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class userControl {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     *
     * 发验证码，没钱,只做了个简化版
     * @return
     */
    @PostMapping("/sendMSG")
    public R<String> sendMSG(@RequestBody User user,HttpSession session)
    {
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone))
        {
            //随机生成验证码,这里将验证码转为String类型，便于以后操作
            String code = ValidateCodeUtils.generateValidateCode(6).toString();

            log.info("验证码是: {}",code);
            //保存验证码到session，以后校验用
            //使用当前phone作为key,便于后面验证
//            session.setAttribute(phone,code);

            //优化，使用redis,并设置存活时间
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);


            //设置存活时间
//            session.setMaxInactiveInterval(120);
            return R.success("生成验证码成功");
        }else
            {
                return R.error("生成验证码失败");
            }


    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session)
    {
        //目前只验证手机号，验证码不检验

        if(map==null)
        {
            return R.error("登陆失败");
        }
        String phone = (String) map.get("phone");

        String  code =(String) map.get("code");
//        String sessionCode = (String) session.getAttribute(phone);
        Object MSGcode = redisTemplate.opsForValue().get(phone);

        //1.验证验证码是否正确，正确，则登录成功
        //这里不应·验证手机，是因为，我们保存验证码时，用的手机号当key,如果手机号不对
        //那获取的验证码也对不上
        if(MSGcode!=null&&code.equals(MSGcode))
        {
            //2.然后查一下数据库中是否有这个用户，没有的话，自动添加注册
            LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(phone!=null,User::getPhone,phone);
            User one = userService.getOne(lambdaUpdateWrapper);
            if(one==null)
            {
                User user = new User();
                user.setPhone(phone);
                userService.save(user);
                session.setAttribute("user",user.getId());
                return R.success(user);
            }
            session.setAttribute("user",one.getId());

            //登录成功时，将redis中的验证码删除
            Boolean delete = redisTemplate.delete(phone);

            return R.success(one);
        }
        //3.验证码不正确，
        return R.error("登录失败");
    }











}
