package com.example.kdt.voucher;

import java.util.UUID;

public class FixAmountVoucher implements Voucher {

    private final UUID voucherId;
    private final long amount;

    public FixAmountVoucher(UUID voucherId, long amount) {
        this.voucherId = voucherId;
        this.amount = amount;
    }

    @Override
    public long discount(long beforeDiscount) {
        long result = beforeDiscount - amount;
        return result < 0 ? 0 : result;
    }

    @Override
    public UUID getVoucherId() {
        return voucherId;
    }

    @Override
    public String toString() {
        return "FixAmountVoucher{" +
                "voucherId=" + voucherId +
                ", amount=" + amount +
                '}';
    }

}
