package com.example.kdt.voucher;

import java.util.UUID;

public class PercentVoucher implements Voucher {

    private final UUID voucherId;
    private final long percent;

    public PercentVoucher(UUID voucherId, long percent) {
        this.voucherId = voucherId;
        this.percent = percent;
    }

    @Override
    public long discount(long beforeDiscount) {
        return (long) (beforeDiscount * ((100 - percent) / 100.0));
    }

    @Override
    public UUID getVoucherId() {
        return voucherId;
    }

}
