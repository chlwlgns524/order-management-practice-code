package com.example.kdt.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {
        "com.example.kdt.configuration",
        "com.example.kdt.order",
        "com.example.kdt.voucher"})

@PropertySource(value = "application.yaml")
public class AppConfiguration {

}
