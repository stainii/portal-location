package be.stijnhooft.portal.location.facades;

import be.stijnhooft.portal.location.cache.CacheService;
import be.stijnhooft.portal.location.services.geocode.GeocodeService;
import be.stijnhooft.portal.model.location.GeocodeResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class GeocodeFacade {

    private final Collection<GeocodeService> geocodeServices;
    private final CacheService cacheService;

    public Optional<GeocodeResult> geocode(@NotNull String locationUserInput) {
        var geocodeResult = geocodeServices.stream()
                .filter(GeocodeService::enabled)
                .sorted(Comparator.comparingInt(GeocodeService::order))
                .flatMap(geocodeService -> safeGeocode(locationUserInput, geocodeService).stream())
                .findFirst();
        geocodeResult.ifPresent(l -> cacheService.addToCacheIfNotPresent(locationUserInput, l));
        return geocodeResult;
    }

    private Optional<GeocodeResult> safeGeocode(String locationUserInput, GeocodeService geocodeService) {
        try {
            return geocodeService.geocode(locationUserInput);
        } catch (Exception e) {
            log.error("Exception during usage of {}", geocodeService.name(), e);
            return Optional.empty();
        }
    }

}
