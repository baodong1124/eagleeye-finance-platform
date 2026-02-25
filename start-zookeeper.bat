@echo off
REM 简化的 Zookeeper 启动脚本，解决 CLASSPATH 长度问题

setlocal
set KAFKA_HOME=D:\program\kafka\kafka_2.12-3.6.1

REM 设置 JVM 参数
set CLASSPATH=%KAFKA_HOME%\libs\*

REM 启动 Zookeeper
"%JAVA_HOME%\bin\java" ^
  -cp "%KAFKA_HOME%\libs\*" ^
  -Dlog4j.configuration=file:%KAFKA_HOME%\config\log4j.properties ^
  -Dzookeeper.log.dir=%KAFKA_HOME%\logs ^
  org.apache.zookeeper.server.quorum.QuorumPeerMain %KAFKA_HOME%\config\zookeeper.properties

endlocal
