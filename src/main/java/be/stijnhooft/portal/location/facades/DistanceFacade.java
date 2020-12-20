package be.stijnhooft.portal.location.facades;

import be.stijnhooft.portal.location.domain.GeocodeResult;
import be.stijnhooft.portal.location.services.DistanceService;
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

    public Optional<Double> calculateDistanceInKm(String location1, String location2) {
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

        return Optional.of(distanceService.calculateDistanceInKm(latitude1, longitude1, latitude2, longitude2));
    }

}
