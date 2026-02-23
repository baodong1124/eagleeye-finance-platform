package com.eagleeye.common.constant;

/**
 * 系统常量
 */
public class Constants {

    /**
     * 字符编码
     */
    public static final String UTF8 = "UTF-8";

    /**
     * 成功标记
     */
    public static final String SUCCESS = "success";

    /**
     * 失败标记
     */
    public static final String FAIL = "fail";

    /**
     * 用户会话key
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 权限token
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * Redis缓存key前缀
     */
    public interface Redis {
        String USER_INFO_KEY = "user:info:";
        String ACCOUNT_BALANCE_KEY = "account:balance:";
        String ACCOUNT_LOCK_KEY = "account:lock:";
        String BUDGET_LOCK_KEY = "budget:lock:";
        String TOKEN_KEY = "token:";
    }

    /**
     * 账户状态
     */
    public interface AccountStatus {
        String NORMAL = "NORMAL";
        String FROZEN = "FROZEN";
        String CLOSED = "CLOSED";
    }

    /**
     * 报销单状态
     */
    public interface ExpenseStatus {
        String DRAFT = "DRAFT";           // 草稿
        String PENDING = "PENDING";       // 待审批
        String APPROVED = "APPROVED";     // 已通过
        String REJECTED = "REJECTED";     // 已拒绝
        String PAID = "PAID";             // 已支付
    }

    /**
     * 审批状态
     */
    public interface ApprovalStatus {
        String PENDING = "PENDING";       // 待审批
        String APPROVED = "APPROVED";     // 已通过
        String REJECTED = "REJECTED";     // 已拒绝
    }

    /**
     * 流水类型
     */
    public interface TransactionType {
        String INCOME = "INCOME";         // 收入
        String EXPENSE = "EXPENSE";       // 支出
        String TRANSFER = "TRANSFER";     // 转账
        String FREEZE = "FREEZE";         // 冻结
        String UNFREEZE = "UNFREEZE";     // 解冻
    }

    /**
     * Kafka主题
     */
    public interface KafkaTopic {
        String EXPENSE_APPROVAL = "expense-approval";
        String ACCOUNT_TRANSACTION = "account-transaction";
        String RECONCILIATION = "reconciliation";
    }
}
