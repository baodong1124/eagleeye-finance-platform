# EagleEye Finance Platform

## 项目简介

EagleEye Finance Platform 是一个模拟集团企业资金管理与费用管控的后端系统，旨在满足金融科技公司对中高级Java开发者的技术要求。项目采用高内聚、低耦合的架构设计，为后续微服务化演进预留空间。

## 技术栈

### 核心技术
- **Java 17** - 基础开发语言
- **Spring Boot 3.2.2** - 应用框架
- **Spring MVC** - Web框架
- **MyBatis-Plus 3.5.5** - ORM框架，高效数据操作

### 数据存储
- **MySQL 8.0** - 主数据库
- **Redis 3.x** - 缓存与分布式锁
- **Druid** - 数据库连接池与监控

### 消息队列
- **Kafka 3.1.2** - 异步消息处理（审批流、对账通知）

### 工具类库
- **Lombok** - 减少样板代码
- **Hutool 5.8.24** - Java工具类库
- **FastJSON2 2.0.43** - JSON处理
- **Apache Commons Lang3** - 通用工具类

### API文档
- **Knife4j 4.3.0** - Swagger UI增强版

### 构建工具
- **Maven** - 项目管理

## 项目结构

```
eagleeye-finance-platform/
├── eagleeye-parent/              # 父POM（依赖管理）
├── eagleeye-common/              # 通用模块（工具、常量、异常、配置）
│   ├── base/                     # 基础类（BaseEntity、BaseQueryDTO）
│   ├── result/                   # 统一响应封装（Result、ResultCode）
│   ├── exception/                # 异常处理（BusinessException、GlobalExceptionHandler）
│   ├── config/                   # 配置类（MybatisPlusConfig、RedisConfig）
│   ├── filter/                   # 过滤器（TraceIdFilter）
│   └── plugin/                   # MyBatis插件（AuditLogInterceptor）
├── eagleeye-system/              # 系统管理模块
│   ├── entity/                   # 实体类（User、Role、Permission、Department）
│   ├── dto/                      # 数据传输对象
│   ├── vo/                       # 视图对象
│   ├── mapper/                   # MyBatis Mapper接口
│   ├── service/                  # 业务层接口及实现
│   └── controller/               # 控制器层
├── eagleeye-account/             # 账户管理模块
│   ├── entity/                   # 实体类（Account、TransactionLog、Budget）
│   ├── mapper/                   # Mapper接口
│   └── service/                  # 业务层（资金账户、预算账户、流水记录）
├── eagleeye-expense/             # 费用报销模块
│   ├── entity/                   # 实体类（ExpenseOrder、ExpenseItem、ApprovalLog）
│   ├── dto/                      # 数据传输对象（ExpenseSubmitDTO）
│   ├── service/                  # 业务层（报销提交、审批流程）
│   └── controller/               # 控制器层
├── eagleeye-payment/             # 支付对账模块
│   ├── entity/                   # 实体类（PaymentRecord、ReconciliationRecord）
│   └── service/                  # 业务层（支付、对账模拟）
├── eagleeye-analysis/            # 数据分析模块
│   ├── entity/                   # 实体类（ExpenseStatistics）
│   └── service/                  # 业务层（数据统计、报表生成）
├── schema.sql                    # 数据库表结构初始化脚本
├── data.sql                      # 基础数据初始化脚本
└── README.md                     # 项目说明文档
```

## 模块说明

### eagleeye-parent
父POM模块，负责管理所有子模块的依赖版本统一。

### eagleeye-common
通用模块，包含：
- 基础实体类和DTO
- 统一API响应封装（`Result<T>`）
- 全局异常处理
- MyBatis-Plus配置（分页插件、乐观锁）
- Redis配置
- 全局请求追踪（TraceId过滤器）
- 审计日志拦截器

### eagleeye-system
系统管理模块，实现RBAC权限控制：
- 用户管理（增删改查、分页查询）
- 角色管理
- 权限管理
- 部门管理

### eagleeye-account
账户管理核心模块：
- 资金账户（账户号、余额、状态、预算维度）
- 预算账户（部门/项目预算管理）
- 账户流水记录
- 分布式事务场景（并发安全，使用乐观锁）

### eagleeye-expense
费用报销核心模块：
- 报销单管理（提交、审批、撤回）
- 报销明细
- 审批流水记录
- **分布式事务场景**：提交报销单并同步扣减预算

### eagleeye-payment
支付对账模块：
- 支付记录管理
- 对账记录管理
- 对账流程模拟

### eagleeye-analysis
数据分析模块：
- 费用统计分析
- 报表生成

## 快速开始

### 环境要求
- JDK 17
- Maven 3.6+
- MySQL 8.0
- Redis 3.x
- Kafka 3.x（可选）

### 1. 克隆项目

```bash
git clone <repository-url>
cd eagleeye-finance-platform
```

### 2. 初始化数据库

创建数据库并执行初始化脚本：

```bash
mysql -u root -p < schema.sql
mysql -u root -p eagleeye_finance < data.sql
```

### 3. 修改配置

编辑 `eagleeye-system/src/main/resources/application.yml`，修改数据库、Redis、Kafka连接信息：

```yaml
spring:
  datasource:
    druid:
      url: jdbc:mysql://localhost:3306/eagleeye_finance?...
      username: your_username
      password: your_password
  data:
    redis:
      host: localhost
      port: 6379
  kafka:
    bootstrap-servers: localhost:9092
```

### 4. 编译打包

```bash
mvn clean install
```

### 5. 启动应用

```bash
# 方式1：使用Maven启动
cd eagleeye-system
mvn spring-boot:run

# 方式2：使用Java命令启动
java -jar eagleeye-system/target/eagleeye-system-1.0.0.jar
```

### 6. 访问系统

- **API文档地址**：http://localhost:8080/doc.html
- **Druid监控地址**：http://localhost:8080/druid（admin/admin）
- **应用地址**：http://localhost:8080

## 核心功能演示

### 1. 系统管理 - 用户CRUD示例

完整的用户管理链路：`UserController -> UserService -> UserMapper`

```java
// 分页查询用户
GET /api/users/page?current=1&size=10

// 创建用户
POST /api/users
{
  "username": "testuser",
  "realName": "测试用户",
  "email": "test@example.com",
  "deptId": 1
}
```

### 2. 费用报销 - 复杂业务场景示例

`ExpenseSubmitService.submitExpense()` 方法展示了以下技术点：

- ✅ 验证预算余额（调用账户模块）
- ✅ 生成分布式唯一ID（雪花算法）
- ✅ Redis分布式锁（防止重复提交或并发扣款）
- ✅ Kafka消息发送（通知审批人）
- ✅ 审计日志记录

```java
// 提交报销单
POST /api/expense/submit?applicantId=5
{
  "expenseType": 1,
  "description": "差旅费报销",
  "items": [
    {
      "itemName": "交通费",
      "amount": 500.00,
      "invoiceType": 2
    }
  ]
}
```

## 核心技术特性

### 1. 全局请求追踪
- 使用 `TraceIdFilter` 为每个请求生成唯一的 `traceId`
- 通过 MDC 贯穿所有日志输出
- 支持链路追踪和问题排查

### 2. 分布式事务场景
- **预算扣减并发控制**：使用乐观锁（`@Version`）确保数据一致性
- **分布式锁**：使用 Redis SETNX 实现分布式锁，防止重复提交
- **消息驱动**：使用 Kafka 实现审批通知异步处理

### 3. 审计日志
- 通过 `AuditLogInterceptor` 拦截 MyBatis 的 insert、update、delete 操作
- 自动记录核心数据表的变更日志

### 4. 统一异常处理
- 全局异常处理器 `GlobalExceptionHandler`
- 统一的错误码枚举 `ResultCode`
- 自定义业务异常 `BusinessException`

### 5. 数据库层面优化
- 合理的索引设计
- 逻辑删除（`deleted` 字段）
- 字段自动填充（创建时间、更新时间）
- 分页查询（MyBatis-Plus 分页插件）

## 后续学习与扩展计划

### 1. 微服务化演进
- [ ] 集成 **Spring Cloud Alibaba**
  - Nacos 服务注册与发现
  - Nacos 配置中心
  - Sentinel 流量控制与熔断降级
  - OpenFeign 服务调用
  - Gateway API网关

### 2. 分布式事务
- [ ] 实现 **TCC分布式事务**
- [ ] 实现 **Seata AT模式**
- [ ] 实现事务消息（RocketMQ/Kafka）

### 3. 认证与授权
- [ ] 集成 **Spring Security** 或 **Apache Shiro**
- [ ] 实现 **JWT Token** 认证
- [ ] 集成 **OAuth2.0**
- [ ] 实现单点登录（SSO）

### 4. 日志系统
- [ ] 集成 **ELK** 日志系统（Elasticsearch + Logstash + Kibana）
- [ ] 集成 **SkyWalking** 或 **Zipkin** 进行分布式链路追踪

### 5. 缓存优化
- [ ] Redis 集群部署
- [ ] 多级缓存（本地缓存 + Redis）
- [ ] 缓存预热与缓存更新策略

### 6. 高并发优化
- [ ] **MySQL调优**
  - 慢查询分析与优化
  - 索引优化
  - 分库分表策略
  - 读写分离
- [ ] **JVM调优**
  - JVM参数调优
  - 内存泄漏排查
- [ ] **限流降级**
  - 接口限流
  - 服务降级
- [ ] **秒杀场景**：预算秒杀功能（Redis + 消息队列）

### 7. 监控告警
- [ ] 集成 **Prometheus + Grafana**
- [ ] 集成 **Micrometer** 指标采集
- [ ] 实现健康检查与告警

### 8. CI/CD
- [ ] 集成 **Jenkins** 或 **GitLab CI**
- [ ] 自动化测试
- [ ] 自动化部署

### 9. 容器化
- [ ] 编写 **Dockerfile**
- [ ] Docker Compose 编排
- [ ] Kubernetes 部署

### 10. 性能测试
- [ ] 使用 **JMeter** 进行压力测试
- [ ] 使用 **wrk** 进行基准测试
- [ ] 性能瓶颈分析与优化

## 联系方式

- 项目作者：EagleEye Team
- 邮箱：contact@eagleeye.com

## 许可证

本项目仅用于学习和交流，请勿用于商业用途。

---

**注意**：本项目为模拟系统，生产环境使用前请进行充分的测试和优化。
