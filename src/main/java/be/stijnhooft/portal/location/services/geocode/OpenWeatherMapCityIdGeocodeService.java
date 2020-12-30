package be.stijnhooft.portal.location.services.geocode;

import be.stijnhooft.portal.model.location.GeocodeResult;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class OpenWeatherMapCityIdGeocodeService implements GeocodeService {

    public static final String CITY_LIST_GZ_FILE = "openweathermap/city.list.json.gz";

    @Value("${be.stijnhooft.portal.location.service.geocode.OpenWeatherMap.order:2}")
    private int order;

    @Value("${be.stijnhooft.portal.location.service.geocode.OpenWeatherMap.enabled:true}")
    private boolean enabled;

    @Override
    public Optional<GeocodeResult> geocode(String locationUserInput) {
        return tokenize(locationUserInput)
                .flatMap(location -> {
                    InputStream cityList = streamCityList();
                    return findGeocodeResult(location, cityList).stream();
                })
                .findFirst();
    }

    private Stream<String> tokenize(String locationUserInput) {
        return Arrays.stream(locationUserInput.split(","))
                .map(String::trim);
    }

    private Optional<GeocodeResult> findGeocodeResult(String locationUserInput, InputStream unzippedCityListStream) {
        JsonFactory jsonFactory = new JsonFactory();
        try (JsonParser jsonParser = jsonFactory.createParser(unzippedCityListStream)) {

            String latitude = null;
            String longitude = null;
            boolean locationFound = false;

            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                JsonToken token = jsonParser.getCurrentToken();
                String key = jsonParser.getCurrentName();

                // is it the end of the json object? Did we found anything? If not, reset.
                if (token.equals(JsonToken.END_OBJECT)) {
                    if (locationFound && latitude != null && longitude != null) {
                        log.info("Found a geocode result for {}", locationUserInput);
                        return Optional.of(GeocodeResult.builder()
                                .latitude(latitude)
                                .longitude(longitude)
                                .build());
                    } else {
                        latitude = null;
                        longitude = null;
                    }
                }

                // is this the location we're looking for?
                if (token.equals(JsonToken.VALUE_STRING) && "name".equals(key)) {
                    String tokenValue = jsonParser.getValueAsString();
                    if (locationUserInput.equals(tokenValue)) {
                        locationFound = true;
                    }
                }


                // or is this the latitude or longitude of the selected location?
                if (token.equals(JsonToken.VALUE_NUMBER_FLOAT)) {
                    if ("lat".equals(key)) {
                        latitude = jsonParser.getValueAsString();
                    } else if ("lon".equals(key)) {
                        longitude = jsonParser.getValueAsString();
                    }
                }
            }

        } catch (IOException e) {
            log.warn("Could not parse the OpenWeatherApi city list.", e);
        }

        log.info("Found no geocode result for {}", locationUserInput);
        return Optional.empty();
    }

    private InputStream streamCityList() {
        try {
            InputStream cityListResourceStream = this.getClass().getResourceAsStream("/" + OpenWeatherMapCityIdGeocodeService.CITY_LIST_GZ_FILE);
            return new GZIPInputStream(cityListResourceStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not unzip the OpenWeatherApi city list.", e);
        }
    }

    @Override
    public String name() {
        return "OpenWeatherMap CityId";
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public int order() {
        return order;
    }

}
