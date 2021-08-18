package com.wangyu;

import com.Context.Autowired;
import com.Context.BeanNameAware;
import com.Context.Component;
import com.Context.Scope;
import lombok.Getter;


@Component("userService")
@Scope("singleton")
public class UserServiceImpl implements UserService {
//    @Autowired
//    private OrderService orderService;
    @Getter
    private String beanName;

    @Override
    public void test() {
        System.out.println("UserService.test");
//        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
