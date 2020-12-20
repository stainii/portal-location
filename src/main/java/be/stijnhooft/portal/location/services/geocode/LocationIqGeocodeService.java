package be.stijnhooft.portal.location.services.geocode;

import be.stijnhooft.portal.location.domain.GeocodeResult;
import be.stijnhooft.portal.location.dto.locationiq.LocationIqResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.Optional;

@Service
@Slf4j
public class LocationIqGeocodeService implements GeocodeService {

    private final int order;
    private final boolean enabled;
    private final String apiKey;
    private final String urlTemplate;
    private final RestTemplate restTemplate;

    public LocationIqGeocodeService(@Value("${be.stijnhooft.portal.location.service.geocode.LocationIQ.order:1}") int order,
                                    @Value("${be.stijnhooft.portal.location.service.geocode.LocationIQ.enabled:true}") boolean enabled,
                                    @Value("${be.stijnhooft.portal.location.service.geocode.LocationIQ.api-key}") String apiKey,
                                    @Value("${be.stijnhooft.portal.location.service.geocode.LocationIQ.url}") String urlTemplate,
                                    RestTemplate restTemplate) {
        this.order = order;
        this.enabled = enabled;
        this.apiKey = apiKey;
        this.urlTemplate = urlTemplate;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<GeocodeResult> geocode(String userInput) {
        String url = MessageFormat.format(urlTemplate, apiKey, userInput);

        try {
            var responses = restTemplate.getForObject(url, LocationIqResponse[].class);
            if (responses != null && responses.length > 0) {
                var mappedResponse = map(responses[0], userInput);
                return Optional.of(mappedResponse);
            }
        } catch(Exception e) {
            log.warn("Could not retrieve geocode result from LocationIQ", e);
        }

        return Optional.empty();
    }

    private GeocodeResult map(LocationIqResponse response, String userInput) {
        return GeocodeResult.builder()
                .userInput(userInput)
                .latitude(response.getLat())
                .longitude(response.getLon())
                .source(name())
                .build();
    }

    @Override
    public String name() {
        return "LocationIQ";
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
