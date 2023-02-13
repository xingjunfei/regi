package com.itheima.Config;
import com.itheima.common.JacksonObjectMapper;
import com.itheima.filter.loginInteceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     *静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {

        /**
         * 如果访问路径是 /backend/** ，就映射到 classpath:/backend/ 这里去找静态资源
         */

        log.info("开始进行静态资源映射...");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptor = registry.addInterceptor(new loginInteceptor()).excludePathPatterns(
                         "/employee/login",
                        "/employee/logout",
                        "/backend/**",
                        "/front/**",
                      "/user/sendMSG/**",
                     "/user/login/**"
        );

    }

    /**
     * 扩展mvc的消息转换器，比如我们在返还给前端数据时，可以将数据的类型进行转化，至于怎么转，还得调用对象转换器
     * mvc本身有默认的转换器，我们这是，给他扩展一个我门自定义的
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建一个消息转换器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器,JacksonObjectMapper()对象转换器是老师提供的
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        //将设置好的消息转换器，加入到mvc的转换器集合中,并设置优先级为0，最高
        converters.add(0,messageConverter);

    }
}
