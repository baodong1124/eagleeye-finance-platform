package com.eagleeye.expense.controller;

import com.eagleeye.common.result.Result;
import com.eagleeye.expense.dto.ExpenseSubmitDTO;
import com.eagleeye.expense.service.ExpenseSubmitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 报销Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
@Tag(name = "报销管理", description = "报销单相关操作")
public class ExpenseController {

    private final ExpenseSubmitService expenseSubmitService;

    @Operation(summary = "提交报销单")
    @PostMapping("/submit")
    public Result<Long> submitExpense(
            @Parameter(description = "申请人ID") @RequestParam Long applicantId,
            @RequestBody ExpenseSubmitDTO dto) {

        log.info("提交报销单，applicantId={}, dto={}", applicantId, dto);
        try {
            Long orderId = expenseSubmitService.submitExpense(applicantId, dto);
            return Result.success(orderId);
        } catch (Exception e) {
            log.error("提交报销单失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "审批报销单")
    @PostMapping("/approve")
    public Result<Void> approveExpense(
            @Parameter(description = "报销单ID") @RequestParam Long orderId,
            @Parameter(description = "审批人ID") @RequestParam Long approverId,
            @Parameter(description = "是否通过") @RequestParam Boolean approved,
            @Parameter(description = "审批意见") @RequestParam(required = false) String comment) {

        log.info("审批报销单，orderId={}, approverId={}, approved={}", orderId, approverId, approved);
        try {
            boolean success = expenseSubmitService.approveExpense(orderId, approverId, approved, comment);
            return success ? Result.success() : Result.error("审批失败");
        } catch (Exception e) {
            log.error("审批报销单失败", e);
            return Result.error(e.getMessage());
        }
    }
}
