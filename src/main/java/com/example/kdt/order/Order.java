package com.example.kdt.order;

import com.example.kdt.voucher.Voucher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Order {

    private final UUID orderId;
    private final UUID customerId;
    private final List<OrderItem> orderItems;
    private Optional<Voucher> voucher;
    private OrderStatus orderStatus = OrderStatus.ACCEPTED;

    public Order(UUID orderId, UUID customerId, List<OrderItem> orderItems) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderItems = orderItems;
        voucher = Optional.empty();
    }

    public Order(UUID orderId, UUID customerId, List<OrderItem> orderItems, Voucher voucher) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderItems = orderItems;
        this.voucher = Optional.of(voucher);
    }

    public long totalAmount() {
        Long beforeDiscount = orderItems.stream()
                .map(v -> v.getProductPrice() * v.getQuantity())
                .reduce(0L, Long::sum);

        if (voucher.isPresent())
            return voucher.get().discount(beforeDiscount);
        else
            return beforeDiscount;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = Optional.of(voucher);
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public UUID getOrderId() {
        return orderId;
    }

}
