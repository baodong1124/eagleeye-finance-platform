-- ========================================
-- EagleEye Finance Platform 数据库初始化脚本
-- 版本：1.0.0
-- 描述：创建系统管理、账户管理、费用报销、支付对账、数据分析相关表
-- ========================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS eagleeye_finance DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE eagleeye_finance;

-- ========================================
-- 1. 系统管理模块表
-- ========================================

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `dept_id` BIGINT DEFAULT NULL COMMENT '部门ID',
    `position` VARCHAR(50) DEFAULT NULL COMMENT '职位',
    `status` TINYINT DEFAULT 1 COMMENT '用户状态：0-禁用，1-启用',
    `balance` DECIMAL(15,2) DEFAULT 0.00 COMMENT '账户余额',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `role_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
    `status` TINYINT DEFAULT 1 COMMENT '角色状态：0-禁用，1-启用',
    `sort` INT DEFAULT 0 COMMENT '排序序号',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`role_id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 权限表
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
    `permission_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `permission_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
    `type` TINYINT DEFAULT 1 COMMENT '权限类型：1-菜单，2-按钮，3-接口',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID',
    `path` VARCHAR(200) DEFAULT NULL COMMENT '路径',
    `status` TINYINT DEFAULT 1 COMMENT '权限状态：0-禁用，1-启用',
    `sort` INT DEFAULT 0 COMMENT '排序序号',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`permission_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 角色权限关联表
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 部门表
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department` (
    `dept_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `dept_name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父部门ID',
    `level` INT DEFAULT 1 COMMENT '部门层级',
    `leader` VARCHAR(50) DEFAULT NULL COMMENT '部门负责人',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '部门地址',
    `status` TINYINT DEFAULT 1 COMMENT '部门状态：0-禁用，1-启用',
    `sort` INT DEFAULT 0 COMMENT '排序序号',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`dept_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- ========================================
-- 2. 账户管理模块表
-- ========================================

-- 账户表
DROP TABLE IF EXISTS `fin_account`;
CREATE TABLE `fin_account` (
    `account_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '账户ID',
    `account_no` VARCHAR(50) NOT NULL COMMENT '账户号',
    `account_name` VARCHAR(100) NOT NULL COMMENT '账户名称',
    `account_type` TINYINT NOT NULL COMMENT '账户类型：1-资金账户，2-预算账户',
    `belong_type` TINYINT NOT NULL COMMENT '归属类型：1-部门，2-项目',
    `belong_id` BIGINT NOT NULL COMMENT '归属ID（部门ID或项目ID）',
    `balance` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '余额',
    `frozen_amount` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
    `status` TINYINT DEFAULT 1 COMMENT '账户状态：0-冻结，1-正常',
    `credit_limit` DECIMAL(15,2) DEFAULT 0.00 COMMENT '信用额度',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `version` INT DEFAULT 0 COMMENT '版本号（乐观锁）',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`account_id`),
    UNIQUE KEY `uk_account_no` (`account_no`),
    KEY `idx_belong` (`belong_type`, `belong_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户表';

-- 账户流水记录表
DROP TABLE IF EXISTS `fin_transaction_log`;
CREATE TABLE `fin_transaction_log` (
    `log_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    `transaction_no` VARCHAR(50) NOT NULL COMMENT '流水号',
    `order_no` VARCHAR(50) DEFAULT NULL COMMENT '关联订单号',
    `account_id` BIGINT NOT NULL COMMENT '账户ID',
    `transaction_type` TINYINT NOT NULL COMMENT '交易类型：1-收入，2-支出，3-冻结，4-解冻',
    `amount` DECIMAL(15,2) NOT NULL COMMENT '金额',
    `before_balance` DECIMAL(15,2) NOT NULL COMMENT '交易前余额',
    `after_balance` DECIMAL(15,2) NOT NULL COMMENT '交易后余额',
    `business_type` TINYINT DEFAULT NULL COMMENT '业务类型：1-报销，2-转账，3-充值，4-提现，5-预算冻结',
    `business_desc` VARCHAR(200) DEFAULT NULL COMMENT '业务描述',
    `status` TINYINT DEFAULT 1 COMMENT '交易状态：0-处理中，1-成功，2-失败',
    `fail_reason` VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`log_id`),
    UNIQUE KEY `uk_transaction_no` (`transaction_no`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_account_id` (`account_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户流水记录表';

-- 预算表
DROP TABLE IF EXISTS `fin_budget`;
CREATE TABLE `fin_budget` (
    `budget_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预算ID',
    `budget_no` VARCHAR(50) NOT NULL COMMENT '预算编号',
    `budget_name` VARCHAR(100) NOT NULL COMMENT '预算名称',
    `budget_type` TINYINT NOT NULL COMMENT '预算类型：1-年度预算，2-季度预算，3-月度预算',
    `budget_year` INT NOT NULL COMMENT '预算年度',
    `budget_month` INT DEFAULT NULL COMMENT '预算月份',
    `belong_type` TINYINT NOT NULL COMMENT '归属类型：1-部门，2-项目',
    `belong_id` BIGINT NOT NULL COMMENT '归属ID（部门ID或项目ID）',
    `total_amount` DECIMAL(15,2) NOT NULL COMMENT '预算总额',
    `used_amount` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '已使用金额',
    `remaining_amount` DECIMAL(15,2) NOT NULL COMMENT '剩余金额',
    `status` TINYINT DEFAULT 1 COMMENT '预算状态：0-草稿，1-生效，2-过期',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`budget_id`),
    UNIQUE KEY `uk_budget_no` (`budget_no`),
    KEY `idx_belong` (`belong_type`, `belong_id`),
    KEY `idx_budget_year` (`budget_year`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预算表';

-- ========================================
-- 3. 费用报销模块表
-- ========================================

-- 报销单表
DROP TABLE IF EXISTS `exp_expense_order`;
CREATE TABLE `exp_expense_order` (
    `order_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报销单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '报销单号',
    `applicant_id` BIGINT NOT NULL COMMENT '申请人ID',
    `applicant_name` VARCHAR(50) NOT NULL COMMENT '申请人姓名',
    `dept_id` BIGINT DEFAULT NULL COMMENT '部门ID',
    `dept_name` VARCHAR(100) DEFAULT NULL COMMENT '部门名称',
    `project_id` BIGINT DEFAULT NULL COMMENT '项目ID',
    `project_name` VARCHAR(100) DEFAULT NULL COMMENT '项目名称',
    `total_amount` DECIMAL(15,2) NOT NULL COMMENT '报销总金额',
    `expense_type` TINYINT NOT NULL COMMENT '报销类型：1-差旅费，2-办公费，3-招待费，4-其他',
    `description` VARCHAR(500) NOT NULL COMMENT '报销说明',
    `expense_date` DATETIME DEFAULT NULL COMMENT '报销日期',
    `approval_status` TINYINT DEFAULT 1 COMMENT '审批状态：0-待提交，1-待审批，2-审批中，3-已通过，4-已拒绝，5-已撤回',
    `current_node` VARCHAR(50) DEFAULT NULL COMMENT '当前审批节点',
    `approver_id` BIGINT DEFAULT NULL COMMENT '审批人ID',
    `approver_name` VARCHAR(50) DEFAULT NULL COMMENT '审批人姓名',
    `approval_comment` VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
    `approval_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `payment_status` TINYINT DEFAULT 0 COMMENT '支付状态：0-未支付，1-支付中，2-已支付',
    `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `attachment_urls` TEXT DEFAULT NULL COMMENT '附件URL（逗号分隔）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`order_id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_applicant_id` (`applicant_id`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_approval_status` (`approval_status`),
    KEY `idx_expense_type` (`expense_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报销单表';

-- 报销明细表
DROP TABLE IF EXISTS `exp_expense_item`;
CREATE TABLE `exp_expense_item` (
    `item_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id` BIGINT NOT NULL COMMENT '报销单ID',
    `item_no` INT NOT NULL COMMENT '明细序号',
    `item_name` VARCHAR(100) NOT NULL COMMENT '费用名称',
    `amount` DECIMAL(15,2) NOT NULL COMMENT '费用金额',
    `expense_date` VARCHAR(20) DEFAULT NULL COMMENT '费用日期',
    `invoice_type` TINYINT DEFAULT NULL COMMENT '发票类型：1-增值税专用发票，2-增值税普通发票，3-其他',
    `invoice_no` VARCHAR(50) DEFAULT NULL COMMENT '发票号码',
    `invoice_image` VARCHAR(500) DEFAULT NULL COMMENT '发票图片URL',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`item_id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报销明细表';

-- 审批流水表
DROP TABLE IF EXISTS `exp_approval_log`;
CREATE TABLE `exp_approval_log` (
    `log_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '审批流水ID',
    `order_id` BIGINT NOT NULL COMMENT '报销单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '报销单号',
    `node` VARCHAR(50) NOT NULL COMMENT '审批节点',
    `approver_id` BIGINT NOT NULL COMMENT '审批人ID',
    `approver_name` VARCHAR(50) NOT NULL COMMENT '审批人姓名',
    `approval_result` TINYINT NOT NULL COMMENT '审批结果：1-通过，2-拒绝，3-撤回',
    `comment` VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
    `approval_time` DATETIME NOT NULL COMMENT '审批时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`log_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_approver_id` (`approver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批流水表';

-- ========================================
-- 4. 支付对账模块表
-- ========================================

-- 支付记录表
DROP TABLE IF EXISTS `pay_payment_record`;
CREATE TABLE `pay_payment_record` (
    `payment_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
    `payment_no` VARCHAR(50) NOT NULL COMMENT '支付单号',
    `order_no` VARCHAR(50) NOT NULL COMMENT '关联订单号',
    `payment_type` TINYINT NOT NULL COMMENT '支付类型：1-报销支付，2-转账，3-提现',
    `amount` DECIMAL(15,2) NOT NULL COMMENT '支付金额',
    `payee_id` BIGINT DEFAULT NULL COMMENT '收款人ID',
    `payee_name` VARCHAR(50) DEFAULT NULL COMMENT '收款人姓名',
    `payee_account` VARCHAR(50) DEFAULT NULL COMMENT '收款人账号',
    `payee_bank` VARCHAR(100) DEFAULT NULL COMMENT '收款银行',
    `status` TINYINT DEFAULT 0 COMMENT '支付状态：0-待支付，1-支付中，2-支付成功，3-支付失败',
    `channel` TINYINT DEFAULT NULL COMMENT '支付渠道：1-银行转账，2-支付宝，3-微信，4-内部账户',
    `transaction_id` VARCHAR(100) DEFAULT NULL COMMENT '第三方支付流水号',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `fail_reason` VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`payment_id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- 对账记录表
DROP TABLE IF EXISTS `pay_reconciliation_record`;
CREATE TABLE `pay_reconciliation_record` (
    `record_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '对账记录ID',
    `reconcile_date` DATE NOT NULL COMMENT '对账日期',
    `reconcile_type` TINYINT NOT NULL COMMENT '对账类型：1-自动对账，2-手动对账',
    `status` TINYINT DEFAULT 0 COMMENT '对账状态：0-进行中，1-成功，2-失败',
    `total_count` INT DEFAULT 0 COMMENT '总笔数',
    `success_count` INT DEFAULT 0 COMMENT '成功笔数',
    `fail_count` INT DEFAULT 0 COMMENT '失败笔数',
    `total_amount` DECIMAL(15,2) DEFAULT 0.00 COMMENT '总金额',
    `diff_amount` DECIMAL(15,2) DEFAULT 0.00 COMMENT '差异金额',
    `diff_desc` TEXT DEFAULT NULL COMMENT '差异说明',
    `operator_id` BIGINT DEFAULT NULL COMMENT '对账人ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '对账人姓名',
    `finish_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`record_id`),
    KEY `idx_reconcile_date` (`reconcile_date`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对账记录表';

-- ========================================
-- 5. 数据分析模块表
-- ========================================

-- 费用统计表
DROP TABLE IF EXISTS `ana_expense_statistics`;
CREATE TABLE `ana_expense_statistics` (
    `stat_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    `stat_date` DATE NOT NULL COMMENT '统计日期',
    `stat_type` TINYINT NOT NULL COMMENT '统计类型：1-按天，2-按周，3-按月，4-按季度，5-按年',
    `belong_type` TINYINT NOT NULL COMMENT '归属类型：1-部门，2-项目，3-个人',
    `belong_id` BIGINT NOT NULL COMMENT '归属ID',
    `belong_name` VARCHAR(100) DEFAULT NULL COMMENT '归属名称',
    `total_amount` DECIMAL(15,2) DEFAULT 0.00 COMMENT '报销总金额',
    `order_count` INT DEFAULT 0 COMMENT '报销单数量',
    `paid_amount` DECIMAL(15,2) DEFAULT 0.00 COMMENT '已支付金额',
    `paid_count` INT DEFAULT 0 COMMENT '已支付数量',
    `avg_amount` DECIMAL(15,2) DEFAULT 0.00 COMMENT '平均报销金额',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`stat_id`),
    UNIQUE KEY `uk_stat` (`stat_date`, `stat_type`, `belong_type`, `belong_id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_belong` (`belong_type`, `belong_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='费用统计表';

-- ========================================
-- 6. 审计日志表
-- ========================================

-- 审计日志表
DROP TABLE IF EXISTS `sys_audit_log`;
CREATE TABLE `sys_audit_log` (
    `log_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `trace_id` VARCHAR(32) DEFAULT NULL COMMENT '追踪ID',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    `operation` VARCHAR(100) NOT NULL COMMENT '操作类型',
    `module` VARCHAR(50) DEFAULT NULL COMMENT '模块名称',
    `table_name` VARCHAR(100) DEFAULT NULL COMMENT '表名',
    `record_id` BIGINT DEFAULT NULL COMMENT '记录ID',
    `old_value` TEXT DEFAULT NULL COMMENT '旧值（JSON格式）',
    `new_value` TEXT DEFAULT NULL COMMENT '新值（JSON格式）',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '操作描述',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`log_id`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_operation` (`operation`),
    KEY `idx_trace_id` (`trace_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';
