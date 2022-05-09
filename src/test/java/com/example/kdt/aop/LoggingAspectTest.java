package com.example.kdt.aop;

import com.example.kdt.voucher.FixAmountVoucher;
import com.example.kdt.voucher.VoucherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.UUID;

@SpringJUnitConfig
class LoggingAspectTest {

    private final static Logger logger = LoggerFactory.getLogger(LoggingAspectTest.class);

    @Configuration
    @EnableAspectJAutoProxy
    @ComponentScan(basePackages = {"com.example.kdt.voucher", "com.example.kdt.aop"})
    static class Config {

    }

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    VoucherService voucherService;

    @Test
    @Order(1)
    @DisplayName("새로운 고객을 데이터베이스에 저장할 수 있다.")
    void testInsert() {
        voucherService.insert(new FixAmountVoucher(UUID.randomUUID(), 100L));
    }

}
