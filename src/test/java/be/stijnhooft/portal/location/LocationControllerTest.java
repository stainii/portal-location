package be.stijnhooft.portal.location;

import be.stijnhooft.portal.location.domain.GeocodeResult;
import be.stijnhooft.portal.location.facades.DistanceFacade;
import be.stijnhooft.portal.location.facades.GeocodeFacade;
import org.ehcache.Cache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DistanceFacade distanceFacade;

    @MockBean
    private GeocodeFacade geocodeFacade;

    @MockBean
    private Cache<String, GeocodeResult> geocodeCache; //.workaround for weird problem that @ActiveProfiles("local") doesn't get picked up

    @Test
    public void geocodeWhenFound() throws Exception {
        var geocodeResult = GeocodeResult.builder()
                .source("test")
                .userInput("Zottegem")
                .longitude("1.1")
                .latitude("2.2")
                .build();
        when(geocodeFacade.geocode("Zottegem")).thenReturn(Optional.of(geocodeResult));

        mockMvc.perform(get("/geocode/?query=Zottegem"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"source\": \"test\", " +
                        "\"userInput\": \"Zottegem\", " +
                        "\"longitude\": \"1.1\", " +
                        "\"latitude\": \"2.2\"" +
                        "}"));

        verify(geocodeFacade).geocode("Zottegem");
        verifyNoMoreInteractions(geocodeFacade);
    }

    @Test
    public void geocodeWhenNotFound() throws Exception {
        when(geocodeFacade.geocode("non-existing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/geocode/?query=non-existing"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(geocodeFacade).geocode("non-existing");
        verifyNoMoreInteractions(geocodeFacade);
    }

    @Test
    public void distanceWhenFound() throws Exception {
        when(distanceFacade.calculateDistanceInKm("Oombergen", "Zottegem")).thenReturn(Optional.of(2.29));

        mockMvc.perform(get("/distance/?location1Query=Oombergen&location2Query=Zottegem"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"km\": 2.29}"));

        verify(distanceFacade).calculateDistanceInKm("Oombergen", "Zottegem");
        verifyNoMoreInteractions(distanceFacade);
    }

    @Test
    public void distanceWhenNotFound() throws Exception {
        when(distanceFacade.calculateDistanceInKm("non-existing", "Zottegem")).thenReturn(Optional.empty());

        mockMvc.perform(get("/distance/?location1Query=non-existing&location2Query=Zottegem"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(distanceFacade).calculateDistanceInKm("non-existing", "Zottegem");
        verifyNoMoreInteractions(distanceFacade);
    }

}
