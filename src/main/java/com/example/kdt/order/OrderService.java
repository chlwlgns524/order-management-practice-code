package com.example.kdt.order;

import com.example.kdt.voucher.Voucher;
import com.example.kdt.voucher.VoucherService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private VoucherService voucherService;

    public OrderService(OrderRepository orderRepository, VoucherService voucherService) {
        this.orderRepository = orderRepository;
        this.voucherService = voucherService;
    }

    public Order createOrder(UUID customerId, List<OrderItem> orderItems) {
        Order order = new Order(UUID.randomUUID(), customerId, orderItems);

        return orderRepository.insert(order);
    }

    public Order createOrder(UUID customerId, List<OrderItem> orderItems, UUID voucherId) {
        Voucher voucher = voucherService.getVoucher(voucherId);
        Order order = new Order(UUID.randomUUID(), customerId, orderItems, voucher);

        return orderRepository.insert(order);
    }

}
