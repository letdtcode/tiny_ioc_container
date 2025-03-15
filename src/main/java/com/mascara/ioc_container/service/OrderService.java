package com.mascara.ioc_container.service;

import com.mascara.ioc_container.annotation.Autowire;
import com.mascara.ioc_container.annotation.Component;
import com.mascara.ioc_container.annotation.PostCostruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderService {

    @Autowire
    private PaymentService paymentService;

    @Autowire
    private RestaurantService restaurantService;

    @PostCostruct
    void postInitiate() {
        log.info("Do something after creating orderService instance");
    }

    public void makeOrder() {
        log.info("Starting make order....");
        paymentService.doSomething();
        restaurantService.doSomething();
        log.info("Making order complete");
    }
}
