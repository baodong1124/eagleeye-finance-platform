# Kafka 本地安装指南 (Windows)

## 方式一：手动下载安装（推荐）

### 1. 下载 Kafka
访问以下链接下载 Kafka（建议使用版本 3.6.1 或更高）：
- 官方下载：https://kafka.apache.org/downloads
- 清华镜像：https://mirrors.tuna.tsinghua.edu.cn/apache/kafka/

选择下载：`kafka_2.12-3.6.1.tgz` 或 `kafka_2.13-3.6.1.tgz` （二进制版本，2.12/2.13 表示 Scala 版本）

### 2. 解压文件
```powershell
# 将下载的文件解压到 D:\program\kafka 目录
tar -xzf kafka_2.12-3.6.1.tgz -C D:\program\kafka
# 或使用解压软件解压到 D:\program\kafka\kafka_2.12-3.6.1
```

### 3. 配置环境变量
设置以下环境变量：
- `KAFKA_HOME=D:\program\kafka\kafka_2.12-3.6.1`
- 将 `%KAFKA_HOME%\bin\windows` 添加到 PATH

### 4. 启动 Kafka

#### 方式一：使用简化启动脚本（推荐，解决"输入行太长"错误）

项目根目录已提供简化启动脚本 `start-zookeeper.bat` 和 `start-kafka.bat`。

**步骤：**
```powershell
# 1. 启动 Zookeeper（新开一个窗口）
cd d:\java\proj\eagleeye-finance-platform
start-zookeeper.bat

# 2. 启动 Kafka（再开一个新窗口）
cd d:\java\proj\eagleeye-finance-platform
start-kafka.bat
```

#### 方式二：使用 Zookeeper 原生脚本（如果方式一不可用）
```powershell
# 1. 启动 Zookeeper（新开一个窗口）
cd D:\program\kafka\kafka_2.12-3.6.1
bin\windows\zookeeper-server-start.bat config\zookeeper.properties

# 2. 启动 Kafka（再开一个新窗口）
cd D:\program\kafka\kafka_2.12-3.6.1
bin\windows\kafka-server-start.bat config\server.properties
```

#### 方式三：使用 Kraft 模式（无需 Zookeeper，需要处理命令行问题）
如果 Kraft 模式命令报错"输入行太长"，请使用方式一的 Zookeeper 模式。

**解决方案 A：使用 CMD（而不是 PowerShell）**
```cmd
REM 打开 CMD 窗口，执行以下命令：
cd D:\program\kafka\kafka_2.12-3.6.1
bin\windows\kafka-storage.bat random-uuid
```

**解决方案 B：直接使用固定 UUID（跳过 random-uuid）**
```powershell
# 使用任意一个固定 UUID 即可，例如：
cd D:\program\kafka\kafka_2.12-3.6.1
bin\windows\kafka-storage.bat format -t O5wMvDyfT0Wv4K3zL4zXqg -c config\kraft\server.properties

# 启动 Kafka
bin\windows\kafka-server-start.bat config\kraft\server.properties
```

### 5. 验证安装

项目根目录已提供简化命令脚本 `kafka-cmd.bat`，解决"输入行太长"错误。

**创建测试主题：**
```powershell
# 方式一：使用简化脚本（推荐）
cd d:\java\proj\eagleeye-finance-platform
.\kafka-cmd.bat kafka.admin.TopicCommand --create --topic test-topic --bootstrap-server localhost:9092

# 方式二：使用原生脚本（如果可用）
cd D:\program\kafka\kafka_2.12-3.6.1
bin\windows\kafka-topics.bat --create --topic test-topic --bootstrap-server localhost:9092
```

**列出主题：**
```powershell
# 使用简化脚本
.\kafka-cmd.bat kafka.admin.TopicCommand --list --bootstrap-server localhost:9092
```

**查看主题详情：**
```powershell
# 使用简化脚本
.\kafka-cmd.bat kafka.admin.TopicCommand --describe --topic test-topic --bootstrap-server localhost:9092
```

**发送消息（生产者）：**
```powershell
# 使用简化脚本
.\kafka-cmd.bat kafka.tools.ConsoleProducer --topic test-topic --broker-list localhost:9092
```

**接收消息（消费者，新开窗口）：**
```powershell
# 使用简化脚本
.\kafka-cmd.bat kafka.tools.ConsoleConsumer --topic test-topic --from-beginning --bootstrap-server localhost:9092
```

**删除主题：**
```powershell
# 使用简化脚本
.\kafka-cmd.bat kafka.admin.TopicCommand --delete --topic test-topic --bootstrap-server localhost:9092
```

### 6. 项目配置
修改 `eagleeye-system/src/main/resources/application-dev.yml`：
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: eagleeye-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

## 方式二：使用 Docker Desktop（最简单）

### 1. 安装 Docker Desktop
下载并安装：https://www.docker.com/products/docker-desktop/

### 2. 启动 Kafka
```powershell
# 启动 Zookeeper 和 Kafka
docker run -d --name zookeeper -p 2181:2181 zookeeper:3.7

docker run -d --name kafka -p 9092:9092 --link zookeeper:zookeeper \
  -e KAFKA_BROKER_ID=0 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:7.3.0
```

### 3. 验证
```powershell
# 查看容器状态
docker ps

# 查看日志
docker logs kafka
```

## 常用命令

### 启动和停止
```powershell
# 停止 Kafka
bin\windows\kafka-server-stop.bat

# 停止 Zookeeper
bin\windows\zookeeper-server-stop.bat
```

### 主题管理
```powershell
# 创建主题
bin\windows\kafka-topics.bat --create --topic my-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# 查看主题详情
bin\windows\kafka-topics.bat --describe --topic my-topic --bootstrap-server localhost:9092

# 删除主题
bin\windows\kafka-topics.bat --delete --topic my-topic --bootstrap-server localhost:9092
```

## 配置文件位置

- Kafka 配置：`config/server.properties` 或 `config/kraft/server.properties`
- Zookeeper 配置：`config/zookeeper.properties`
- 日志目录：默认在 `logs/` 目录

## 注意事项

1. **端口占用**：确保 9092 (Kafka) 和 2181 (Zookeeper) 端口未被占用
2. **内存设置**：如果机器内存较小，可以修改启动脚本中的 JVM 参数
3. **日志清理**：定期清理日志文件避免磁盘占满
4. **防火墙**：允许 Kafka 相关端口通过防火墙

## 故障排查

### 无法启动
- 检查端口是否被占用：`netstat -ano | findstr 9092`
- 查看日志文件：`logs/server.log`

### 连接失败
- 确认 Kafka 服务已启动
- 检查配置文件中的监听地址
- 检查防火墙设置

## 参考资料

- Kafka 官方文档：https://kafka.apache.org/documentation/
- Spring Kafka 文档：https://docs.spring.io/spring-kafka/reference/
