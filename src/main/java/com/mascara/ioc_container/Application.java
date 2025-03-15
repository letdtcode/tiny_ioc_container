package com.mascara.ioc_container;

import com.mascara.ioc_container.annotation.Autowire;
import com.mascara.ioc_container.annotation.Component;
import com.mascara.ioc_container.loader.ContextLoader;
import com.mascara.ioc_container.loader.Runner;
import com.mascara.ioc_container.service.OrderService;
import com.mascara.ioc_container.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Application implements Runner {
    @Autowire
    private OrderService orderService;

    public static void main(String[] args) {
        ContextLoader.getInstance().load("com.mascara.ioc_container");
    }

    @Override
    public void run() {
        log.info("Application is ready to start");
        orderService.makeOrder();
        RestaurantService restaurantService = ContextLoader.getInstance()
                .getBean(RestaurantService.class);
        restaurantService.logToday();
    }
}
