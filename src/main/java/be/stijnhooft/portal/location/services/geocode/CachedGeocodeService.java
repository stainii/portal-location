package be.stijnhooft.portal.location.services.geocode;

import be.stijnhooft.portal.location.domain.GeocodeResult;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CachedGeocodeService implements GeocodeService {

    private final Cache<String, GeocodeResult> geocodeCache;

    public CachedGeocodeService(@Qualifier("geocodeCache") Cache<String, GeocodeResult> geocodeCache) {
        this.geocodeCache = geocodeCache;
    }

    @Override
    public Optional<GeocodeResult> geocode(String locationUserInput) {
        var location = Optional.ofNullable(geocodeCache.get(locationUserInput));
        location.ifPresent(l -> log.info("Found a cached geocode result for user input {}", locationUserInput));
        return location;
    }

    @Override
    public String name() {
        return getClass().getSimpleName();
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public boolean enabled() {
        return true;
    }

}
