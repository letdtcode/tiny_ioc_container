package com.mascara.ioc_container.service;

import com.mascara.ioc_container.annotation.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentService {
    public void doSomething() {
        log.info("Payment service does something");
    }
}
