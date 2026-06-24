# 社团管理系统 - 部署指南

## 环境要求

- Docker 20.10+
- Docker Compose 2.0+
- 域名 (可选，用于 HTTPS)
- SSL 证书 (可选，用于 HTTPS)

## 快速部署

### 1. 服务器准备

```bash
# 安装 Docker
curl -fsSL https://get.docker.com | bash

# 安装 Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 启动 Docker
systemctl start docker
systemctl enable docker
```

### 2. 项目配置

```bash
# 拉取代码
git clone <你的后端仓库地址> backend
git clone <你的前端仓库地址> club_app

# 复制部署文件
cp .env.example .env
cp deploy/backend/Dockerfile backend/
cp deploy/mysql/schema.sql backend/src/main/resources/db/
```

### 3. 修改后端代码

**3.1 添加 MySQL 驱动依赖 (pom.xml)**

在 `<dependencies>` 中添加:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

**3.2 替换配置文件 (src/main/resources/)**

```bash
# 备份原配置
mv application.yml application.yml.bak

# 创建新配置
cat > application.yml << 'EOF'
server:
  port: 8080

spring:
  application:
    name: club-manage-backend
  datasource:
    url: jdbc:mysql://${DB_HOST:mysql}:${DB_PORT:3306}/${DB_NAME:club_manage}?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:}
  sql:
    init:
      mode: never

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

club:
  jwt:
    secret: ${JWT_SECRET:}
    expiration-ms: 86400000
  checkin:
    default-radius-meters: 200

logging:
  level:
    root: WARN
    com.clubmanage: INFO
EOF
```

**3.3 更新 MySQL schema**

用 `deploy/mysql/schema.sql` 替换 `backend/src/main/resources/db/schema.sql`

### 4. 配置环境变量

```bash
vim .env
```

修改以下配置:
- `JWT_SECRET`: JWT 密钥 (至少256位)
- `MYSQL_ROOT_PASSWORD`: MySQL root 密码
- `MYSQL_PASSWORD`: 应用数据库密码

### 5. 构建和启动

```bash
# 构建后端镜像
cd backend
docker build -t club-backend:latest .

# 返回项目根目录
cd ..

# 构建前端
cd club_app
npm install
npm run build
cd ..

# 启动所有服务
docker compose up -d
```

### 6. 初始化管理员账号

首次启动后，管理员账号已自动创建:

- 用户名: `admin`
- 密码: `admin123`

**首次登录后请立即修改密码！**

## HTTPS 配置 (可选)

### 1. 申请 SSL 证书

使用 Let's Encrypt 免费证书:

```bash
certbot --nginx -d your-domain.com
```

### 2. 配置证书路径

将证书复制到指定目录:

```bash
mkdir -p deploy/nginx/cert
cp /etc/letsencrypt/live/your-domain.com/fullchain.pem deploy/nginx/cert/server.crt
cp /etc/letsencrypt/live/your-domain.com/privkey.pem deploy/nginx/cert/server.key
```

### 3. 更新 Nginx 配置

使用 `deploy/nginx/nginx-https.conf` 替换 `deploy/nginx/nginx.conf`

## 目录结构

```
/workspace/
├── backend/                 # 后端 Spring Boot
│   ├── src/main/resources/
│   │   └── db/schema.sql    # MySQL 建表脚本
│   └── Dockerfile
├── club_app/                # 前端 (需自行配置)
├── deploy/
│   ├── backend/
│   │   ├── application-dev.yml
│   │   ├── application-prod.yml
│   │   └── Dockerfile
│   ├── mysql/
│   │   └── schema.sql
│   └── nginx/
│       ├── nginx.conf
│       └── nginx-https.conf
├── dist/                    # 前端构建产物
├── docker-compose.yml
├── .env                     # 环境变量 (不提交到 git)
└── .env.example
```

## 常用命令

```bash
# 查看日志
docker compose logs -f

# 查看后端日志
docker compose logs -f backend

# 重启服务
docker compose restart

# 停止服务
docker compose down

# 重建服务
docker compose up -d --force-recreate
```

## 防火墙配置

```bash
# 开放端口
 firewall-cmd --permanent --add-port=80/tcp
 firewall-cmd --permanent --add-port=443/tcp
 firewall-cmd --permanent --add-port=3306/tcp  # MySQL (仅内网访问)
 firewall-cmd --reload
```

## 备份

```bash
# 备份数据库
docker compose exec mysql mysqldump -u root -p club_manage > backup_$(date +%Y%m%d).sql

# 备份数据卷
docker run --rm -v workspace_mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql_backup.tar.gz /data
```
