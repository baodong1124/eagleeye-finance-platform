package com.eagleeye.expense.controller;

import com.eagleeye.account.service.BudgetService;
import com.eagleeye.common.result.Result;
import com.eagleeye.expense.dto.ExpenseSubmitDTO;
import com.eagleeye.expense.service.ExpenseSubmitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 报销Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
@Tag(name = "报销管理", description = "报销单相关操作")
@SecurityRequirement(name = "Authorization")
public class ExpenseController {

    private final ExpenseSubmitService expenseSubmitService;
    private final BudgetService budgetService;

    @Operation(summary = "提交报销单")
    @PostMapping("/submit")
    public Result<Long> submitExpense(
            @Parameter(description = "申请人ID") @RequestParam(name = "applicantId") Long applicantId,
            @Parameter(description = "并发控制方案：1-数据库悲观锁，2-内存原子操作", required = false) @RequestParam(name = "concurrencyStrategy", defaultValue = "1") Integer concurrencyStrategy,
            @RequestBody ExpenseSubmitDTO dto) {

        log.info("提交报销单，applicantId={}, concurrencyStrategy={}, dto={}", applicantId, concurrencyStrategy, dto);
        try {
            Long orderId = expenseSubmitService.submitExpense(applicantId, dto, concurrencyStrategy);
            return Result.success(orderId);
        } catch (Exception e) {
            log.error("提交报销单失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "审批报销单")
    @PostMapping("/approve")
    public Result<Void> approveExpense(
            @Parameter(description = "报销单ID") @RequestParam(name = "orderId") Long orderId,
            @Parameter(description = "审批人ID") @RequestParam(name = "approverId") Long approverId,
            @Parameter(description = "是否通过") @RequestParam(name = "approved") Boolean approved,
            @Parameter(description = "审批意见") @RequestParam(name = "comment", required = false) String comment) {

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
