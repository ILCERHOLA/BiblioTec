# ---- Etapa 1: compilar el WAR con Maven y Java 25 ----
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

# ---- Etapa 2: ejecutar en Tomcat 10.1 con Java 25 ----
FROM tomcat:10.1-jre25-temurin-noble
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/bibliotec.war /usr/local/tomcat/webapps/ROOT.war

# Memoria acotada para el plan gratuito (512 MB)
ENV CATALINA_OPTS="-Xms64m -Xmx300m -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m"

EXPOSE 8080
# Render indica el puerto en la variable PORT; si no existe se usa 8080
CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT:-8080}\\\"/\" /usr/local/tomcat/conf/server.xml && exec catalina.sh run"]
