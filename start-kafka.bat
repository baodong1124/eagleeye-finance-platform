@echo off
REM 简化的 Kafka 启动脚本，解决 CLASSPATH 长度问题

setlocal
set KAFKA_HOME=D:\program\kafka\kafka_2.12-3.6.1

REM 设置 JVM 参数
if ["%KAFKA_HEAP_OPTS%"] EQU [""] (
    set KAFKA_HEAP_OPTS=-Xmx1G -Xms1G
)

REM 启动 Kafka
"%JAVA_HOME%\bin\java" ^
  %KAFKA_HEAP_OPTS% ^
  -cp "%KAFKA_HOME%\libs\*" ^
  -Dlog4j.configuration=file:%KAFKA_HOME%\config\log4j.properties ^
  -Dkafka.logs.dir=%KAFKA_HOME%\logs ^
  -Djava.io.tmpdir=%KAFKA_HOME%\logs ^
  kafka.Kafka %KAFKA_HOME%\config\server.properties

endlocal
