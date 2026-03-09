# First stage, build the custom JRE
FROM eclipse-temurin:21-jdk-alpine AS jre-builder

RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /optimized-jdk-21

# Second stage, Use the custom JRE and build the app image
FROM alpine:latest
ENV JAVA_HOME=/opt/jdk/jdk-21
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-builder /optimized-jdk-21 $JAVA_HOME

ARG APPLICATION_USER=spring
RUN addgroup --system $APPLICATION_USER &&  adduser --system $APPLICATION_USER --ingroup $APPLICATION_USER
COPY --chown=$APPLICATION_USER:$APPLICATION_USER target/*.jar /application.jar
USER $APPLICATION_USER

ENTRYPOINT [ "java", "-jar", "/application.jar" ]