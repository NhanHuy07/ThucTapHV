package com.dev.dungcony.modules.users.controllers.admin;

import com.dev.dungcony.commons.dtos.ApiRes;
import com.dev.dungcony.modules.users.dto.req.RankAddReq;
import com.dev.dungcony.modules.users.dto.req.RankUpdateReq;
import com.dev.dungcony.modules.users.dto.res.RankRes;
import com.dev.dungcony.modules.users.enums.RankType;
import com.dev.dungcony.modules.users.services.interfaces.rank.RankCreateService;
import com.dev.dungcony.modules.users.services.interfaces.rank.RankGetService;
import com.dev.dungcony.modules.users.services.interfaces.rank.RankUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "RANK")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/admin/rank")
public class RankController {

    private final RankGetService rankGetService;
    private final RankUpdateService rankUpdateService;
    private final RankCreateService rankCreateService;

    @Operation(summary = "Lấy rank theo loại")
    @GetMapping("/get")
    public ResponseEntity<ApiRes<RankRes>> getRank(
            @RequestParam RankType type) {
        return ResponseEntity.ok(
                ApiRes.success("rank", rankGetService.getRank(type)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo rank")
    @PostMapping("/create")
    public ResponseEntity<ApiRes<String>> createRank(
            @Valid @RequestBody RankAddReq req) {
        return ResponseEntity.ok(
                ApiRes.success("rank created", rankCreateService.createRank(req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật rank")
    @PutMapping("/update")
    public ResponseEntity<ApiRes<RankRes>> updateRank(
            @RequestParam RankType type,
            @Valid @RequestBody RankUpdateReq req) {
        return ResponseEntity.ok(
                ApiRes.success("rank updated", rankUpdateService.update(type, req)));
    }
}
