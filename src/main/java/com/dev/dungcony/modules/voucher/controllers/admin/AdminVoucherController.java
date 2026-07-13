package com.dev.dungcony.modules.voucher.controllers.admin;


import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.voucher.dto.req.CreateVoucherReq;
import com.dev.dungcony.modules.voucher.dto.req.VoucherUpdateReq;
import com.dev.dungcony.modules.voucher.dto.res.VoucherRes;
import com.dev.dungcony.modules.voucher.services.interfaces.VoucherCreateService;
import com.dev.dungcony.modules.voucher.services.interfaces.VoucherUpdateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin/voucher")
@Tag(name = "Voucher (Admin)")
public class AdminVoucherController {

    private final VoucherCreateService voucherCreateService;
    private final VoucherUpdateService voucherUpdateService;

    @PostMapping("create")
    public ResponseEntity<ApiRes<VoucherRes>> createVoucher(@Valid @RequestBody CreateVoucherReq req) {
        return ResponseEntity.ok(ApiRes.success("Voucher created", voucherCreateService.createVoucher(req)));
    }


    @PutMapping("/update")
    public ResponseEntity<ApiRes<Void>> updateVoucherStatus(
            @RequestParam("voucher-code") String code,
            @RequestBody VoucherUpdateReq req) {
        voucherUpdateService.update(code, req);
        return ResponseEntity.ok(ApiRes.success("Voucher status updated"));
    }

}
