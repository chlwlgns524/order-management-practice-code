package com.example.kdt.order;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

@Component
public class OrderProperties implements InitializingBean {

    @Value("${kdt-version:0}")
    private Integer version;

    @Value("${kdt-support-vendors}")
    private List<String> vendors;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(MessageFormat.format("version: {0}",version ));
        System.out.println(MessageFormat.format("vendors: {0}", vendors));
    }

}
