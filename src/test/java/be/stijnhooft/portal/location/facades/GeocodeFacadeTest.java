package be.stijnhooft.portal.location.facades;

import be.stijnhooft.portal.location.cache.CacheService;
import be.stijnhooft.portal.location.services.geocode.CachedGeocodeService;
import be.stijnhooft.portal.location.services.geocode.LocationIqGeocodeService;
import be.stijnhooft.portal.location.services.geocode.OpenWeatherMapCityIdGeocodeService;
import be.stijnhooft.portal.model.location.GeocodeResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class GeocodeFacadeTest {

    @Autowired
    private GeocodeFacade geocodeFacade;

    @MockBean
    private CacheService cacheService;

    @MockBean
    private CachedGeocodeService cachedGeocodeService;

    @MockBean
    private LocationIqGeocodeService geocodeService1;

    @MockBean
    private OpenWeatherMapCityIdGeocodeService geocodeService2;

    @Test
    void geocodeServicesAreCalledInOrder() {
        // arrange
        var expectedResult = GeocodeResult.builder().build();

        when(cachedGeocodeService.enabled()).thenReturn(true);
        when(cachedGeocodeService.order()).thenReturn(0);
        when(cachedGeocodeService.geocode("Aalst")).thenReturn(Optional.empty());

        when(geocodeService1.enabled()).thenReturn(true);
        when(geocodeService1.order()).thenReturn(1);
        when(geocodeService1.geocode("Aalst")).thenReturn(Optional.empty());

        when(geocodeService2.enabled()).thenReturn(true);
        when(geocodeService2.order()).thenReturn(2);
        when(geocodeService2.geocode("Aalst")).thenReturn(Optional.of(expectedResult));

        // act
        var result = geocodeFacade.geocode("Aalst");

        // assert
        verify(cachedGeocodeService).enabled();
        verify(cachedGeocodeService).order();
        verify(cachedGeocodeService).geocode("Aalst");

        verify(geocodeService1).enabled();
        verify(geocodeService1, times(2)).order();
        verify(geocodeService1).geocode("Aalst");

        verify(geocodeService2).enabled();
        verify(geocodeService2).order();
        verify(geocodeService2).geocode("Aalst");

        verify(cacheService).addToCacheIfNotPresent("Aalst", expectedResult);

        verifyNoMoreInteractions(cachedGeocodeService, geocodeService1, geocodeService2, cacheService);

        assertTrue(result.isPresent());
        assertEquals(expectedResult, result.get());
    }

    @Test
    void firstResultGetsReturned() {
        // arrange
        var expectedResult = GeocodeResult.builder().build();

        when(cachedGeocodeService.enabled()).thenReturn(true);
        when(cachedGeocodeService.order()).thenReturn(0);
        when(cachedGeocodeService.geocode("Aalst")).thenReturn(Optional.empty());

        when(geocodeService1.enabled()).thenReturn(true);
        when(geocodeService1.order()).thenReturn(1);
        when(geocodeService1.geocode("Aalst")).thenReturn(Optional.of(expectedResult));

        when(geocodeService2.enabled()).thenReturn(true);
        when(geocodeService2.order()).thenReturn(2);

        // act
        var result = geocodeFacade.geocode("Aalst");

        // assert
        verify(cachedGeocodeService).enabled();
        verify(cachedGeocodeService).order();
        verify(cachedGeocodeService).geocode("Aalst");

        verify(geocodeService1).enabled();
        verify(geocodeService1, times(2)).order();
        verify(geocodeService1).geocode("Aalst");

        verify(geocodeService2).enabled();
        verify(geocodeService2).order();

        verify(cacheService).addToCacheIfNotPresent("Aalst", expectedResult);

        verifyNoMoreInteractions(cachedGeocodeService, geocodeService1, geocodeService2, cacheService);

        assertTrue(result.isPresent());
        assertEquals(expectedResult, result.get());
    }

    @Test
    void whenNothingIsFoundAnEmptyOptionalIsReturned() {
        // arrange
        when(cachedGeocodeService.enabled()).thenReturn(true);
        when(cachedGeocodeService.order()).thenReturn(0);
        when(cachedGeocodeService.geocode("Aalst")).thenReturn(Optional.empty());

        when(geocodeService1.enabled()).thenReturn(true);
        when(geocodeService1.order()).thenReturn(1);
        when(geocodeService1.geocode("Aalst")).thenReturn(Optional.empty());

        when(geocodeService2.enabled()).thenReturn(true);
        when(geocodeService2.order()).thenReturn(2);
        when(geocodeService2.geocode("Aalst")).thenReturn(Optional.empty());

        // act
        var result = geocodeFacade.geocode("Aalst");

        // assert
        verify(cachedGeocodeService).enabled();
        verify(cachedGeocodeService).order();
        verify(cachedGeocodeService).geocode("Aalst");

        verify(geocodeService1).enabled();
        verify(geocodeService1, times(2)).order();
        verify(geocodeService1).geocode("Aalst");

        verify(geocodeService2).enabled();
        verify(geocodeService2).order();
        verify(geocodeService2).geocode("Aalst");

        verifyNoMoreInteractions(cachedGeocodeService, geocodeService1, geocodeService2, cacheService);

        assertFalse(result.isPresent());
    }


    @Test
    void disabledGeocodeServicesAreNotCalled() {
        // arrange
        var expectedResult = GeocodeResult.builder().build();

        when(cachedGeocodeService.enabled()).thenReturn(true);
        when(cachedGeocodeService.order()).thenReturn(0);
        when(cachedGeocodeService.geocode("Aalst")).thenReturn(Optional.empty());

        when(geocodeService1.enabled()).thenReturn(false);
        when(geocodeService1.order()).thenReturn(1);

        when(geocodeService2.enabled()).thenReturn(true);
        when(geocodeService2.order()).thenReturn(2);
        when(geocodeService2.geocode("Aalst")).thenReturn(Optional.of(expectedResult));

        // act
        var result = geocodeFacade.geocode("Aalst");

        // assert
        verify(cachedGeocodeService).enabled();
        verify(cachedGeocodeService).order();
        verify(cachedGeocodeService).geocode("Aalst");

        verify(geocodeService1).enabled();

        verify(geocodeService2).enabled();
        verify(geocodeService2).order();
        verify(geocodeService2).geocode("Aalst");

        verify(cacheService).addToCacheIfNotPresent("Aalst", expectedResult);

        verifyNoMoreInteractions(cachedGeocodeService, geocodeService1, geocodeService2, cacheService);

        assertTrue(result.isPresent());
        assertEquals(expectedResult, result.get());
    }

    @Test
    void anExceptionWontStopTheFlow() {
        // arrange
        var expectedResult = GeocodeResult.builder().build();

        when(cachedGeocodeService.enabled()).thenReturn(true);
        when(cachedGeocodeService.order()).thenReturn(0);
        when(cachedGeocodeService.geocode("Aalst")).thenReturn(Optional.empty());

        when(geocodeService1.enabled()).thenReturn(true);
        when(geocodeService1.order()).thenReturn(1);
        when(geocodeService1.geocode("Aalst")).thenThrow(new IllegalArgumentException("This exception should not stop the flow"));
        when(geocodeService1.name()).thenReturn("1");

        when(geocodeService2.enabled()).thenReturn(true);
        when(geocodeService2.order()).thenReturn(2);
        when(geocodeService2.geocode("Aalst")).thenReturn(Optional.of(expectedResult));

        // act
        var result = geocodeFacade.geocode("Aalst");

        // assert
        verify(cachedGeocodeService).enabled();
        verify(cachedGeocodeService).order();
        verify(cachedGeocodeService).geocode("Aalst");

        verify(geocodeService1).enabled();
        verify(geocodeService1, times(2)).order();
        verify(geocodeService1).geocode("Aalst");
        verify(geocodeService1).name();

        verify(geocodeService2).enabled();
        verify(geocodeService2).order();
        verify(geocodeService2).geocode("Aalst");

        verify(cacheService).addToCacheIfNotPresent("Aalst", expectedResult);

        verifyNoMoreInteractions(cachedGeocodeService, geocodeService1, geocodeService2, cacheService);

        assertTrue(result.isPresent());
        assertEquals(expectedResult, result.get());
    }

}