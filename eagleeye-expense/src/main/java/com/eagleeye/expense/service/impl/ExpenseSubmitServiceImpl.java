package com.eagleeye.expense.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.eagleeye.account.entity.Budget;
import com.eagleeye.account.entity.TransactionLog;
import com.eagleeye.account.service.AccountService;
import com.eagleeye.account.service.BudgetService;
import com.eagleeye.account.service.TransactionLogService;
import com.eagleeye.expense.dto.ExpenseSubmitDTO;
import com.eagleeye.expense.entity.ApprovalLog;
import com.eagleeye.expense.entity.ExpenseItem;
import com.eagleeye.expense.entity.ExpenseOrder;
import com.eagleeye.expense.service.ExpenseSubmitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 报销单提交Service实现类
 * 演示分布式事务、并发控制、消息驱动等核心场景
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseSubmitServiceImpl implements ExpenseSubmitService {

    private final BudgetService budgetService;
    private final TransactionLogService transactionLogService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Kafka主题名称
    private static final String EXPENSE_APPROVAL_TOPIC = "expense-approval";

    // Redis分布式锁key前缀
    private static final String EXPENSE_LOCK_PREFIX = "expense:lock:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitExpense(Long applicantId, ExpenseSubmitDTO dto) {
        // TODO: 此处需要验证申请人信息

        // 计算报销总金额
        BigDecimal totalAmount = dto.getItems().stream()
                .map(ExpenseSubmitDTO.ExpenseItemDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String orderNo = generateOrderNo();

        // 分布式锁key
        String lockKey = EXPENSE_LOCK_PREFIX + orderNo;

        try {
            // ========================================
            // 此处需要加分布式锁，防止重复提交或并发扣款
            // ========================================
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 30, TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(locked)) {
                throw new RuntimeException("系统繁忙，请稍后重试");
            }

            // ========================================
            // 此处需要验证预算余额（调用账户模块）
            // ========================================
            Budget budget = budgetService.getActiveBudget(1, applicantId); // 1表示部门
            if (budget == null) {
                throw new RuntimeException("未找到预算信息");
            }

            // 检查预算余额是否充足
            if (!budgetService.checkBudgetBalance(budget.getBudgetId(), totalAmount)) {
                throw new RuntimeException("预算余额不足，当前可用：" + budget.getRemainingAmount());
            }

            // ========================================
            // 此处需要生成分布式唯一ID（用于订单号）
            // ========================================
            String uniqueOrderId = generateSnowflakeId().toString();

            // 冻结预算金额
            boolean frozen = budgetService.freezeBudget(budget.getBudgetId(), totalAmount);
            if (!frozen) {
                throw new RuntimeException("冻结预算失败");
            }

            // 创建报销单
            ExpenseOrder order = createExpenseOrder(applicantId, orderNo, totalAmount, dto);
            // TODO: 保存报销单到数据库
            Long orderId = order.getOrderId();

            // 创建报销明细
            for (int i = 0; i < dto.getItems().size(); i++) {
                ExpenseSubmitDTO.ExpenseItemDTO itemDTO = dto.getItems().get(i);
                createExpenseItem(orderId, i + 1, itemDTO);
                // TODO: 保存报销明细到数据库
            }

            // ========================================
            // 此处需要发送Kafka消息，通知审批人
            // ========================================
            sendApprovalMessage(order);

            // ========================================
            // 此处需要记录审计日志
            // ========================================
            recordAuditLog(applicantId, "SUBMIT_EXPENSE", "提交报销单，订单号：" + orderNo);

            log.info("提交报销单成功，orderId={}, orderNo={}, amount={}", orderId, orderNo, totalAmount);
            return orderId;

        } finally {
            // 释放分布式锁
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveExpense(Long orderId, Long approverId, Boolean approved, String comment) {
        // TODO: 实现审批逻辑
        // 1. 查询报销单
        // 2. 验证审批权限
        // 3. 如果通过，扣减预算（使用已冻结的金额）
        // 4. 如果拒绝，解冻预算
        // 5. 更新报销单状态
        // 6. 记录审批流水
        // 7. 发送审批结果通知
        // 8. 记录审计日志

        log.info("审批报销单，orderId={}, approverId={}, approved={}", orderId, approverId, approved);
        return true;
    }

    /**
     * 生成订单号（日期+随机数）
     */
    private String generateOrderNo() {
        return "EXP" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }

    /**
     * 生成雪花ID（分布式唯一ID）
     */
    private Long generateSnowflakeId() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        return snowflake.nextId();
    }

    /**
     * 创建报销单对象
     */
    private ExpenseOrder createExpenseOrder(Long applicantId, String orderNo, BigDecimal totalAmount, ExpenseSubmitDTO dto) {
        ExpenseOrder order = new ExpenseOrder();
        order.setOrderNo(orderNo);
        order.setApplicantId(applicantId);
        // TODO: 设置申请人姓名
        // TODO: 设置部门信息
        order.setProjectId(dto.getProjectId());
        order.setTotalAmount(totalAmount);
        order.setExpenseType(dto.getExpenseType());
        order.setDescription(dto.getDescription());
        order.setExpenseDate(dto.getExpenseDate());
        order.setApprovalStatus(1); // 待审批
        order.setPaymentStatus(0); // 未支付
        if (dto.getAttachmentUrls() != null && !dto.getAttachmentUrls().isEmpty()) {
            order.setAttachmentUrls(String.join(",", dto.getAttachmentUrls()));
        }
        order.setRemark(dto.getRemark());
        return order;
    }

    /**
     * 创建报销明细对象
     */
    private ExpenseItem createExpenseItem(Long orderId, Integer itemNo, ExpenseSubmitDTO.ExpenseItemDTO itemDTO) {
        ExpenseItem item = new ExpenseItem();
        item.setOrderId(orderId);
        item.setItemNo(itemNo);
        item.setItemName(itemDTO.getItemName());
        item.setAmount(itemDTO.getAmount());
        item.setExpenseDate(itemDTO.getExpenseDate());
        item.setInvoiceType(itemDTO.getInvoiceType());
        item.setInvoiceNo(itemDTO.getInvoiceNo());
        item.setInvoiceImage(itemDTO.getInvoiceImage());
        item.setRemark(itemDTO.getRemark());
        return item;
    }

    /**
     * 发送审批通知消息到Kafka
     */
    private void sendApprovalMessage(ExpenseOrder order) {
        try {
            String message = String.format("{\"orderNo\":\"%s\",\"applicantId\":\"%d\",\"amount\":\"%s\"}",
                    order.getOrderNo(), order.getApplicantId(), order.getTotalAmount());
            kafkaTemplate.send(EXPENSE_APPROVAL_TOPIC, message);
            log.info("发送审批通知成功，orderNo={}", order.getOrderNo());
        } catch (Exception e) {
            log.error("发送审批通知失败，orderNo={}", order.getOrderNo(), e);
            // 消息发送失败不影响主流程，可采用补偿机制
        }
    }

    /**
     * 记录审计日志
     */
    private void recordAuditLog(Long operatorId, String operation, String description) {
        // TODO: 调用审计日志服务记录操作日志
        log.info("记录审计日志：operatorId={}, operation={}, description={}", operatorId, operation, description);
    }
}
