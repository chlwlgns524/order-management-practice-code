package com.example.kdt.voucher;

import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Service
public class VoucherService {

    private VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public Voucher getVoucher(UUID voucherId) {
        return voucherRepository
                .findById(voucherId)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find the voucher for {0}", voucherId)));
    }

    public Voucher insert(Voucher voucher) {
        return voucherRepository.insert(voucher);
    }

}
