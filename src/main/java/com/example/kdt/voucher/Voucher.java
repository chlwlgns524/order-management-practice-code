package com.example.kdt.voucher;

import java.util.UUID;

public interface Voucher {

    long discount(long beforeDiscount);

    UUID getVoucherId();

}
