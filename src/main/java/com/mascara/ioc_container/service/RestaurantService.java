package com.mascara.ioc_container.service;

import com.mascara.ioc_container.annotation.Component;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
@Component
public class RestaurantService {
    public void doSomething() {
        log.info("Restaurant service does something");
    }

    public void logToday() {
        log.info("Today: {}", Instant.now());
    }
}
