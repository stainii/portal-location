package be.stijnhooft.portal.location;

import be.stijnhooft.portal.location.facades.DistanceFacade;
import be.stijnhooft.portal.location.facades.GeocodeFacade;
import be.stijnhooft.portal.model.location.Distance;
import be.stijnhooft.portal.model.location.DistanceQuery;
import be.stijnhooft.portal.model.location.GeocodeResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public ResponseEntity<Distance> distance(DistanceQuery distanceQuery) {
        log.info("Determining distance between {} and {}.", distanceQuery.getLocation1Query(), distanceQuery.getLocation2Query());
        return distanceFacade.calculateDistanceInKm(distanceQuery.getLocation1Query(), distanceQuery.getLocation2Query())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/distance")
    public ResponseEntity<List<Distance>> distances(@RequestBody Set<DistanceQuery> distanceQueryParams) {
        log.info("Determining distance between following: {}", distanceQueryParams);
        var distances = distanceQueryParams.stream()
                .flatMap(distanceQueryParam -> distanceFacade.calculateDistanceInKm(distanceQueryParam.getLocation1Query(), distanceQueryParam.getLocation2Query()).stream())
                .collect(Collectors.toList());
        if (distances.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(distances);
        }
    }
}
