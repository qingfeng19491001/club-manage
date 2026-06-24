# 后端 Dockerfile —— 使用官方 Maven 镜像进行构建（避免 ./mvnw 缺失 / mvn 未安装问题）
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 只复制 pom.xml 先，便于 Docker 缓存依赖层
COPY pom.xml .
RUN mvn -B -e -q dependency:go-offline

# 再复制源码并打包
COPY src ./src
RUN mvn -B -e -q package -DskipTests

# 运行镜像（保持精简）
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN mkdir -p /app/data

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# 默认启用 prod profile，可通过环境变量覆盖
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", "app.jar"]
