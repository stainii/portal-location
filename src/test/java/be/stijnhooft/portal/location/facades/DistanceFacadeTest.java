package be.stijnhooft.portal.location.facades;

import be.stijnhooft.portal.location.services.DistanceService;
import be.stijnhooft.portal.model.location.Distance;
import be.stijnhooft.portal.model.location.GeocodeResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistanceFacadeTest {

    @InjectMocks
    private DistanceFacade distanceFacade;

    @Mock
    private DistanceService distanceService;

    @Mock
    private GeocodeFacade geocodeFacade;

    @Test
    void calculateDistanceInKmWhenSuccess() {
        // arrange
        GeocodeResult geocodeResult1 = new GeocodeResult("Zottegem", "1", "2", "LocationIQ");
        GeocodeResult geocodeResult2 = new GeocodeResult("Aalst", "3", "4", "LocationIQ");

        when(geocodeFacade.geocode("Zottegem")).thenReturn(Optional.of(geocodeResult1));
        when(geocodeFacade.geocode("Aalst")).thenReturn(Optional.of(geocodeResult2));
        when(distanceService.calculateDistanceInKm(1.0, 2.0, 3.0, 4.0)).thenReturn(100.0);

        // act
        Optional<Distance> result = distanceFacade.calculateDistanceInKm("Zottegem", "Aalst");

        // assert
        verify(geocodeFacade).geocode("Zottegem");
        verify(geocodeFacade).geocode("Aalst");
        verify(distanceService).calculateDistanceInKm(1.0, 2.0, 3.0, 4.0);
        verifyNoMoreInteractions(geocodeFacade, distanceService);

        assertEquals(100.0, result.get().getKm());
        assertEquals("Zottegem", result.get().getLocation1Query());
        assertEquals("Aalst", result.get().getLocation2Query());
    }

    @Test
    void calculateDistanceInKmWhenFirstLocationCantBeGeocoded() {
        // arrange
        when(geocodeFacade.geocode("Zottegem")).thenReturn(Optional.empty());

        // act
        Optional<Distance> result = distanceFacade.calculateDistanceInKm("Zottegem", "Aalst");

        // assert
        verify(geocodeFacade).geocode("Zottegem");
        verifyNoMoreInteractions(geocodeFacade, distanceService);

        assertTrue(result.isEmpty());
    }

    @Test
    void calculateDistanceInKmWhenSecondLocationCantBeGeocoded() {
        // arrange
        GeocodeResult geocodeResult1 = new GeocodeResult("Zottegem", "1", "2", "LocationIQ");

        when(geocodeFacade.geocode("Zottegem")).thenReturn(Optional.of(geocodeResult1));
        when(geocodeFacade.geocode("Aalst")).thenReturn(Optional.empty());


        // act
        Optional<Distance> result = distanceFacade.calculateDistanceInKm("Zottegem", "Aalst");

        // assert
        verify(geocodeFacade).geocode("Zottegem");
        verify(geocodeFacade).geocode("Aalst");
        verifyNoMoreInteractions(geocodeFacade, distanceService);

        assertTrue(result.isEmpty());
    }
}