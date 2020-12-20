package be.stijnhooft.portal.location.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistanceServiceTest {

    private DistanceService distanceService;

    @BeforeEach
    void setUp() {
        distanceService = new DistanceService();
    }

    @Test
    void calculateDistanceInKm() {
        double distanceInKm = distanceService.calculateDistanceInKm(50.93337473731394, 4.04507152852168, 50.87131432859628, 3.806988659184265);
        assertEquals(18.06, distanceInKm, 0.01);
    }

}