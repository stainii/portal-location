package be.stijnhooft.portal.location;

import be.stijnhooft.portal.location.domain.Distance;
import be.stijnhooft.portal.location.domain.GeocodeResult;
import be.stijnhooft.portal.location.dto.DistanceQueryParams;
import be.stijnhooft.portal.location.facades.DistanceFacade;
import be.stijnhooft.portal.location.facades.GeocodeFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class LocationController {

    private final GeocodeFacade geocodeFacade;
    private final DistanceFacade distanceFacade;

    @GetMapping("/geocode")
    public ResponseEntity<GeocodeResult> geocode(@RequestParam("query") String query) {
        log.info("Geocoding {}.", query);
        return geocodeFacade.geocode(query)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/distance")
    public ResponseEntity<Distance> distance(DistanceQueryParams distanceQueryParams) {
        return distanceFacade.calculateDistanceInKm(distanceQueryParams.getLocation1Query(), distanceQueryParams.getLocation2Query())
                .map(km -> Distance.builder().km(km).build())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
