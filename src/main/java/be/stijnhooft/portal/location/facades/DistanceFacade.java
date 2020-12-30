package be.stijnhooft.portal.location.facades;

import be.stijnhooft.portal.location.services.DistanceService;
import be.stijnhooft.portal.model.location.Distance;
import be.stijnhooft.portal.model.location.GeocodeResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class DistanceFacade {

    private final GeocodeFacade geocodeFacade;
    private final DistanceService distanceService;

    public Optional<Distance> calculateDistanceInKm(String location1, String location2) {
        Optional<GeocodeResult> geocodeResult1 = geocodeFacade.geocode(location1);
        if (geocodeResult1.isEmpty()) {
            return Optional.empty();
        }

        Optional<GeocodeResult> geocodeResult2 = geocodeFacade.geocode(location2);
        if (geocodeResult2.isEmpty()) {
            return Optional.empty();
        }

        double latitude1 = Double.parseDouble(geocodeResult1.get().getLatitude());
        double longitude1 = Double.parseDouble(geocodeResult1.get().getLongitude());
        double latitude2 = Double.parseDouble(geocodeResult2.get().getLatitude());
        double longitude2 = Double.parseDouble(geocodeResult2.get().getLongitude());

        var km = distanceService.calculateDistanceInKm(latitude1, longitude1, latitude2, longitude2);
        return Optional.of(Distance.builder()
                .km(km)
                .location1Query(location1)
                .location2Query(location2)
                .build());
    }

}
