ARG SOURCE_IMAGE

FROM $SOURCE_IMAGE

COPY webhookproxy*.jar webhookproxy.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "/webhookproxy.jar"]