# ============================================================
# 阶段1: Maven 编译构建
# 使用 Maven + JDK17 镜像来编译 Spring Boot 项目
# 敏感配置通过环境变量注入，运行时使用 --env-file .env 加载
# ============================================================
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# 先复制 pom.xml，利用 Docker 缓存层加速构建
# 只复制依赖定义文件，让依赖下载层被缓存
COPY pom.xml .

# 下载所有依赖（此层会被缓存，代码变更时不会重新下载）
# --batch-mode: 非交互模式  --fail-never: 不因缺失源码失败
RUN mvn dependency:go-offline -B --fail-never

# 复制源代码
COPY src ./src

# 编译打包，跳过测试（Docker 环境无数据库连接）
# -DskipTests: 跳过测试执行  -Dmaven.test.skip=true: 连测试编译也跳过
RUN mvn clean package -DskipTests -Dmaven.test.skip=true -B

# ============================================================
# 阶段2: 运行时镜像
# 使用精简 JRE17 镜像，减小最终镜像体积
# ============================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 创建非 root 用户运行应用（安全最佳实践）
RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -D appuser

# 创建上传文件目录
RUN mkdir -p /app/uploads && chown -R appuser:appgroup /app

# 从构建阶段复制 jar 包
# 使用通配符确保即使版本号变化也能正确复制
COPY --from=builder /build/target/lost-found-*.jar app.jar

# 切换到非 root 用户
USER appuser

# 暴露后端服务端口
EXPOSE 8088

# 启动命令：使用 prod 配置文件
# JVM 参数说明:
#   -XX:+UseContainerSupport: 启用容器内存感知
#   -XX:MaxRAMPercentage=75.0: JVM 最大使用容器内存的 75%
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar", \
    "--spring.profiles.active=prod"]
