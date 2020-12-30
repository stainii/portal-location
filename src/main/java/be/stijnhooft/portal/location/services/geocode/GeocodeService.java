package be.stijnhooft.portal.location.services.geocode;

import be.stijnhooft.portal.model.location.GeocodeResult;

import java.util.Optional;

public interface GeocodeService {

    Optional<GeocodeResult> geocode(String locationUserInput);
    String name();
    int order();
    boolean enabled();

}
