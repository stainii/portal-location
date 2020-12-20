package be.stijnhooft.portal.location.cache;

import be.stijnhooft.portal.location.domain.GeocodeResult;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final Cache<String, GeocodeResult> geocodeCache;

    public CacheService(@Qualifier("geocodeCache") Cache<String, GeocodeResult> geocodeCache) {
        this.geocodeCache = geocodeCache;
    }

    public void clear() {
        geocodeCache.clear();
    }

    public void addToCacheIfNotPresent(String locationUserInput, GeocodeResult geocodeResult) {
        geocodeCache.putIfAbsent(locationUserInput, geocodeResult);
    }

}
