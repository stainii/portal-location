FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT exec java ${JAVA_OPTS_LOCATION} \
 org.springframework.boot.loader.JarLauncher \
 --be.stijnhooft.portal.location.cache.path=${CACHE_PATH} \
 --be.stijnhooft.portal.location.cache.geocode.max-mb=${CACHE_GEOCODE_MAX_MB} \
 --be.stijnhooft.portal.location.cache.geocode.max-no-of-entries=${CACHE_GEOCODE_MAX_NO_OF_ENTRIES} \
 --be.stijnhooft.portal.location.service.geocode.LocationIQ.enabled=${LOCATION_IQ_ENABLED} \
 --be.stijnhooft.portal.location.service.geocode.LocationIQ.order=${LOCATION_IQ_ORDER} \
 --be.stijnhooft.portal.location.service.geocode.LocationIQ.api-key=${LOCATION_IQ_API_KEY} \
 --be.stijnhooft.portal.location.service.geocode.OpenWeatherMap.enabled=${OPEN_WEATHER_MAP_ENABLED} \
 --be.stijnhooft.portal.location.service.geocode.OpenWeatherMap.order=${OPEN_WEATHER_MAP_ORDER}
 --eureka.client.service-url.defaultZone=${EUREKA_SERVICE_URL}