# chronus-console镜像
#
# 需要配置以下环境变量：
#   CHRONUS_REGISTRY_ZOOKEEPER_ADDRESS
#   SPRING_DATA_MONGODB_URI
#   SPRING_DATA_MONGODB_DATABASE
FROM openjdk:8-jdk-alpine

COPY chronus-console/target/chronus-console-*.jar /chronus-console/app.jar

WORKDIR /chronus-console/

ENTRYPOINT ["java", "-jar", "app.jar"]