# chronus-app镜像
#
# 需要配置以下环境变量：
#   CHRONUS_REGISTRY_ZOOKEEPER_ADDRESS
#   SPRING_DATA_MONGODB_URI
#   SPRING_DATA_MONGODB_DATABASE
#   DUBBO_REGISTRY_ADDRESS
FROM openjdk:8-jdk-alpine

COPY chronus-app/target/chronus-app-*.jar /chronus-app/app.jar

WORKDIR /chronus-app/

ENTRYPOINT ["java", "-jar", "app.jar"]