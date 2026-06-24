# 后端 Dockerfile
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# 复制 pom.xml 和源代码
COPY pom.xml .
COPY src ./src

# 下载依赖 (利用 Docker 缓存)
RUN ./mvnw dependency:go-offline -B || mvn dependency:go-offline -B

# 构建
RUN ./mvnw package -DskipTests -B || mvn package -DskipTests -B

# 运行镜像
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 创建数据目录
RUN mkdir -p /app/data

# 复制 jar
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
