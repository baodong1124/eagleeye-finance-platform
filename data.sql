-- ========================================
-- EagleEye Finance Platform 初始化数据脚本
-- 版本：1.0.0
-- 描述：插入基础测试数据
-- ========================================

USE eagleeye_finance;

-- ========================================
-- 1. 初始化部门数据
-- ========================================
INSERT INTO `sys_department` (`dept_id`, `dept_name`, `parent_id`, `level`, `leader`, `phone`, `status`, `sort`) VALUES
(1, '总公司', 0, 1, '张总', '13800138001', 1, 1),
(2, '研发中心', 1, 2, '李总', '13800138002', 1, 1),
(3, '财务中心', 1, 2, '王总', '13800138003', 1, 2),
(4, '人事中心', 1, 2, '赵总', '13800138004', 1, 3),
(5, '市场中心', 1, 2, '钱总', '13800138005', 1, 4),
(6, '后端开发组', 2, 3, '周经理', '13800138006', 1, 1),
(7, '前端开发组', 2, 3, '吴经理', '13800138007', 1, 2),
(8, '测试组', 2, 3, '郑经理', '13800138008', 1, 3),
(9, '会计组', 3, 3, '冯经理', '13800138009', 1, 1),
(10, '出纳组', 3, 3, '陈经理', '13800138010', 1, 2);

-- ========================================
-- 2. 初始化角色数据
-- ========================================
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_code`, `description`, `status`, `sort`) VALUES
(1, '超级管理员', 'ROLE_ADMIN', '系统超级管理员，拥有所有权限', 1, 1),
(2, '财务主管', 'ROLE_FINANCE_MANAGER', '财务部门主管，负责费用审批和预算管理', 1, 2),
(3, '财务专员', 'ROLE_FINANCE_STAFF', '财务部门专员，负责日常财务操作', 1, 3),
(4, '部门主管', 'ROLE_DEPT_MANAGER', '各部门主管，负责本部门报销审批', 1, 4),
(5, '普通员工', 'ROLE_EMPLOYEE', '普通员工，可提交报销申请', 1, 5);

-- ========================================
-- 3. 初始化权限数据
-- ========================================
INSERT INTO `sys_permission` (`permission_id`, `permission_name`, `permission_code`, `type`, `parent_id`, `path`, `status`, `sort`, `icon`) VALUES
-- 一级菜单
(1, '系统管理', 'system', 1, 0, '/system', 1, 1, 'system'),
(2, '费用报销', 'expense', 1, 0, '/expense', 1, 2, 'expense'),
(3, '账户管理', 'account', 1, 0, '/account', 1, 3, 'account'),
(4, '支付对账', 'payment', 1, 0, '/payment', 1, 4, 'payment'),
(5, '数据分析', 'analysis', 1, 0, '/analysis', 1, 5, 'analysis'),

-- 系统管理子菜单
(11, '用户管理', 'system:user', 1, 1, '/system/user', 1, 1, 'user'),
(12, '角色管理', 'system:role', 1, 1, '/system/role', 1, 2, 'role'),
(13, '权限管理', 'system:permission', 1, 1, '/system/permission', 1, 3, 'permission'),
(14, '部门管理', 'system:department', 1, 1, '/system/department', 1, 4, 'department'),

-- 费用报销子菜单
(21, '报销申请', 'expense:apply', 1, 2, '/expense/apply', 1, 1, 'apply'),
(22, '我的报销', 'expense:my', 1, 2, '/expense/my', 1, 2, 'my'),
(23, '报销审批', 'expense:approve', 1, 2, '/expense/approve', 1, 3, 'approve'),
(24, '报销查询', 'expense:query', 1, 2, '/expense/query', 1, 4, 'query'),

-- 账户管理子菜单
(31, '账户列表', 'account:list', 1, 3, '/account/list', 1, 1, 'list'),
(32, '流水记录', 'account:transaction', 1, 3, '/account/transaction', 1, 2, 'transaction'),
(33, '预算管理', 'account:budget', 1, 3, '/account/budget', 1, 3, 'budget'),

-- 按钮权限
(111, '添加用户', 'system:user:add', 2, 11, NULL, 1, 1, NULL),
(112, '编辑用户', 'system:user:edit', 2, 11, NULL, 1, 2, NULL),
(113, '删除用户', 'system:user:delete', 2, 11, NULL, 1, 3, NULL),
(114, '查询用户', 'system:user:query', 2, 11, NULL, 1, 4, NULL),

(211, '提交报销', 'expense:submit', 2, 21, NULL, 1, 1, NULL),
(212, '撤销报销', 'expense:withdraw', 2, 21, NULL, 1, 2, NULL),

(231, '通过审批', 'expense:pass', 2, 23, NULL, 1, 1, NULL),
(232, '拒绝审批', 'expense:reject', 2, 23, NULL, 1, 2, NULL);

-- ========================================
-- 4. 初始化角色权限关联数据
-- ========================================
-- 超级管理员拥有所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, permission_id FROM `sys_permission`;

-- 财务主管权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(2, 2), (2, 21), (2, 22), (2, 23), (2, 24), (2, 211), (2, 212), (2, 231), (2, 232),
(2, 3), (2, 31), (2, 32), (2, 33),
(2, 4), (2, 5);

-- 财务专员权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(3, 2), (3, 21), (3, 22), (3, 24), (3, 211), (3, 212),
(3, 3), (3, 31), (3, 32), (3, 33),
(3, 4);

-- 部门主管权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(4, 2), (4, 21), (4, 22), (4, 23), (4, 24), (4, 211), (4, 212), (4, 231), (4, 232);

-- 普通员工权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(5, 2), (5, 21), (5, 22), (5, 24), (5, 211), (5, 212);

-- ========================================
-- 5. 初始化用户数据
-- ========================================
INSERT INTO `sys_user` (`user_id`, `username`, `password`, `real_name`, `email`, `phone`, `gender`, `dept_id`, `position`, `status`, `balance`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', 'admin@eagleeye.com', '13800138001', 1, 1, '总经理', 1, 0.00),
(2, 'finance_mgr', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四', 'finance@eagleeye.com', '13800138002', 1, 3, '财务主管', 1, 0.00),
(3, 'finance_staff', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王五', 'finstaff@eagleeye.com', '13800138003', 1, 3, '财务专员', 1, 0.00),
(4, 'dept_mgr', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵六', 'dept@eagleeye.com', '13800138004', 1, 2, '研发主管', 1, 0.00),
(5, 'employee1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '孙七', 'employee1@eagleeye.com', '13800138005', 1, 6, '后端工程师', 1, 0.00),
(6, 'employee2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '周八', 'employee2@eagleeye.com', '13800138006', 1, 6, '后端工程师', 1, 0.00),
(7, 'employee3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '吴九', 'employee3@eagleeye.com', '13800138007', 1, 7, '前端工程师', 1, 0.00);

-- ========================================
-- 6. 初始化用户角色关联数据
-- ========================================
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),  -- admin -> 超级管理员
(2, 2),  -- finance_mgr -> 财务主管
(3, 3),  -- finance_staff -> 财务专员
(4, 4),  -- dept_mgr -> 部门主管
(5, 5),  -- employee1 -> 普通员工
(6, 5),  -- employee2 -> 普通员工
(7, 5);  -- employee3 -> 普通员工

-- ========================================
-- 7. 初始化账户数据
-- ========================================
INSERT INTO `fin_account` (`account_no`, `account_name`, `account_type`, `belong_type`, `belong_id`, `balance`, `frozen_amount`, `status`, `credit_limit`) VALUES
('ACC2024001', '研发中心资金账户', 1, 1, 2, 100000.00, 0.00, 1, 50000.00),
('ACC2024002', '财务中心资金账户', 1, 1, 3, 200000.00, 0.00, 1, 100000.00),
('ACC2024003', '市场中心资金账户', 1, 1, 5, 150000.00, 0.00, 1, 80000.00),
('ACC2024004', '研发中心预算账户', 2, 1, 2, 500000.00, 0.00, 1, 0.00),
('ACC2024005', '财务中心预算账户', 2, 1, 3, 300000.00, 0.00, 1, 0.00),
('ACC2024006', '市场中心预算账户', 2, 1, 5, 400000.00, 0.00, 1, 0.00);

-- ========================================
-- 8. 初始化预算数据
-- ========================================
INSERT INTO `fin_budget` (`budget_no`, `budget_name`, `budget_type`, `budget_year`, `budget_month`, `belong_type`, `belong_id`, `total_amount`, `used_amount`, `remaining_amount`, `status`) VALUES
('BUD202401', '研发中心2024年预算', 1, 2024, NULL, 1, 2, 6000000.00, 150000.00, 5850000.00, 1),
('BUD202402', '财务中心2024年预算', 1, 2024, NULL, 1, 3, 4000000.00, 80000.00, 3920000.00, 1),
('BUD202403', '市场中心2024年预算', 1, 2024, NULL, 1, 5, 5000000.00, 200000.00, 4800000.00, 1),
('BUD202404', '研发中心2024年1月预算', 3, 2024, 1, 1, 2, 500000.00, 50000.00, 450000.00, 1),
('BUD202405', '研发中心2024年2月预算', 3, 2024, 2, 1, 2, 500000.00, 30000.00, 470000.00, 1);
