package be.stijnhooft.portal.location.services.geocode;

import be.stijnhooft.portal.model.location.GeocodeResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OpenWeatherMapCityIdGeocodeServiceTest {

    private OpenWeatherMapCityIdGeocodeService service;

    @BeforeEach
    void setUp() {
        this.service = new OpenWeatherMapCityIdGeocodeService();
    }

    @Test
    void geocodeWhenProvidingValidCity() {
        Optional<GeocodeResult> result = service.geocode("Zottegem");
        assertTrue(result.isPresent());
        assertEquals("50.86956", result.get().getLatitude());
        assertEquals("3.81052", result.get().getLongitude());
    }

    @Test
    void geocodeWhenProvidingValidCityAndCountry() {
        Optional<GeocodeResult> result = service.geocode("Zottegem, België");
        assertTrue(result.isPresent());
        assertEquals("50.86956", result.get().getLatitude());
        assertEquals("3.81052", result.get().getLongitude());
    }

    @Test
    void geocodeWhenProvidingValidStreetAndCityAndCountry() {
        Optional<GeocodeResult> result = service.geocode("Stationstraat 42, Zottegem, België");
        assertTrue(result.isPresent());
        assertEquals("50.86956", result.get().getLatitude());
        assertEquals("3.81052", result.get().getLongitude());
    }

    @Test
    void geocodeWhenNothingFound() {
        Optional<GeocodeResult> result = service.geocode("Timboektoe");
        assertFalse(result.isPresent());
    }

}