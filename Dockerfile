FROM openjdk:8u151-jre-slim-stretch

COPY build/libs/app.jar app.jar
# Uncomment to expose a JMX-port on 8090 and then
# add jmx args to java -jar app
# ENV HOST_HOSTNAME="localhost"
# RUN chmod 600 /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/management/jmxremote.password
# EXPOSE 8090
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap"
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar ./app.jar
