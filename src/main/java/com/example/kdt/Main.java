package com.example.kdt;

import com.example.kdt.configuration.AppConfiguration;
import com.example.kdt.order.Order;
import com.example.kdt.order.OrderItem;
import com.example.kdt.order.OrderService;
import com.example.kdt.voucher.FixAmountVoucher;
import com.example.kdt.voucher.Voucher;
import com.example.kdt.voucher.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.UUID;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(AppConfiguration.class);

        logger.trace("logger name: {}", logger.getName());
        logger.debug("logger name: {}", logger.getName());
        logger.info("logger name: {}", logger.getName());
        logger.warn("logger name: {}", logger.getName());
        logger.error("logger error: {}", logger.getClass());


        VoucherRepository voucherRepository = applicationContext.getBean(VoucherRepository.class);
        Voucher voucher = voucherRepository.insert(new FixAmountVoucher(UUID.randomUUID(), 100L));

        OrderService orderService = applicationContext.getBean(OrderService.class);

        Order order = orderService.createOrder(UUID.randomUUID(),
                Arrays.asList(new OrderItem(UUID.randomUUID(), 200L, 1L),
                        new OrderItem(UUID.randomUUID(), 150L, 1L)),
                voucher.getVoucherId());

        System.out.println(order.totalAmount());


    }
}
