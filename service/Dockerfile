FROM 804775010343.dkr.ecr.cn-north-1.amazonaws.com.cn/maven:jdk11-0.0.2
ENV TZ=Asia/Shanghai
COPY . /usr/src/mafia
WORKDIR /usr/src/mafia
ENTRYPOINT ["java", "-jar", "target/service-1.0.0-SNAPSHOT.jar"]
CMD [""]