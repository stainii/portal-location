package be.stijnhooft.portal.location.services.geocode;

import be.stijnhooft.portal.location.domain.GeocodeResult;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class LocationIqGeocodeServiceTest {

    private LocationIqGeocodeService service;
    private WireMockServer wireMockServer;
    private String apiKey = "api-key";

    @LocalServerPort
    private Integer port;

    @Autowired
    private RestTemplate restTemplate;


    @BeforeEach
    public void beforeEach() {
        String locationIqUrl = "http://localhost:" + (port + 1) + "/search.php?key={0}&q={1}&format=json";
        wireMockServer = new WireMockServer(port + 1);
        wireMockServer.start();

        service = new LocationIqGeocodeService(1, true, apiKey, locationIqUrl, restTemplate);
    }

    @AfterEach
    public void afterEach() {
        this.wireMockServer.stop();
    }

    @Test
    void geocodeWhenSomethingFound() {
        var expectedUrl = "/search.php?key=" + apiKey + "&q=Oombergen,%20Zottegem&format=json";
        wireMockServer.stubFor(
                WireMock.get(expectedUrl)
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("[\n" +
                                        "    {\n" +
                                        "        \"place_id\": \"236438001\",\n" +
                                        "        \"licence\": \"https://locationiq.com/attribution\",\n" +
                                        "        \"osm_type\": \"relation\",\n" +
                                        "        \"osm_id\": \"3395550\",\n" +
                                        "        \"boundingbox\": [\n" +
                                        "            \"50.8942745\",\n" +
                                        "            \"50.9163486\",\n" +
                                        "            \"3.8228569\",\n" +
                                        "            \"3.8594213\"\n" +
                                        "        ],\n" +
                                        "        \"lat\": \"50.9052037\",\n" +
                                        "        \"lon\": \"3.83728836703881\",\n" +
                                        "        \"display_name\": \"Oombergen, Zottegem, East Flanders, Flanders, 9620, Belgium\",\n" +
                                        "        \"class\": \"boundary\",\n" +
                                        "        \"type\": \"administrative\",\n" +
                                        "        \"importance\": 0.474365194683011,\n" +
                                        "        \"icon\": \"https://locationiq.org/static/images/mapicons/poi_boundary_administrative.p.20.png\"\n" +
                                        "    },\n" +
                                        "    {\n" +
                                        "        \"place_id\": \"2428349\",\n" +
                                        "        \"licence\": \"https://locationiq.com/attribution\",\n" +
                                        "        \"osm_type\": \"node\",\n" +
                                        "        \"osm_id\": \"344976287\",\n" +
                                        "        \"boundingbox\": [\n" +
                                        "            \"50.8788376\",\n" +
                                        "            \"50.9188376\",\n" +
                                        "            \"3.8181185\",\n" +
                                        "            \"3.8581185\"\n" +
                                        "        ],\n" +
                                        "        \"lat\": \"50.8988376\",\n" +
                                        "        \"lon\": \"3.8381185\",\n" +
                                        "        \"display_name\": \"Oombergen, East Flanders, Flanders, 9620, Belgium\",\n" +
                                        "        \"class\": \"place\",\n" +
                                        "        \"type\": \"village\",\n" +
                                        "        \"importance\": 0.364365194683011,\n" +
                                        "        \"icon\": \"https://locationiq.org/static/images/mapicons/poi_place_village.p.20.png\"\n" +
                                        "    }\n" +
                                        "]"))
        );

        Optional<GeocodeResult> geocodeResult = service.geocode("Oombergen, Zottegem");

        assertTrue(geocodeResult.isPresent());
        assertEquals("50.9052037", geocodeResult.get().getLatitude());
        assertEquals("3.83728836703881", geocodeResult.get().getLongitude());

        wireMockServer.verify(getRequestedFor(urlEqualTo(expectedUrl)));
    }

    @Test
    void geocodeWhenNothingFound() {
        var expectedUrl = "/search.php?key=" + apiKey + "&q=non-existing&format=json";
        wireMockServer.stubFor(
                WireMock.get(expectedUrl)
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\n" +
                                        "    \"error\": \"Unable to geocode\"\n" +
                                        "}")
                                .withStatus(404))
        );

        Optional<GeocodeResult> geocodeResult = service.geocode("non-existing");
        assertFalse(geocodeResult.isPresent());

        wireMockServer.verify(getRequestedFor(urlEqualTo(expectedUrl)));
    }

    @Test
    void geocodeWhen404() {
        var expectedUrl = "/search.php?key=" + apiKey + "&q=Oombergen,%20Zottegem&format=json";
        wireMockServer.stubFor(
                WireMock.get(expectedUrl)
                        .willReturn(aResponse().withStatus(404))
        );

        Optional<GeocodeResult> geocodeResult = service.geocode("Oombergen, Zottegem");
        assertFalse(geocodeResult.isPresent());

        wireMockServer.verify(getRequestedFor(urlEqualTo(expectedUrl)));
    }

    @Test
    void geocodeWhen500() {
        var expectedUrl = "/search.php?key=" + apiKey + "&q=Oombergen,%20Zottegem&format=json";
        wireMockServer.stubFor(
                WireMock.get(expectedUrl)
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\n" +
                                        "    \"error\": \"Unable to geocode\"\n" +
                                        "}")
                                .withStatus(500))
        );

        Optional<GeocodeResult> geocodeResult = service.geocode("Oombergen, Zottegem");
        assertFalse(geocodeResult.isPresent());

        wireMockServer.verify(getRequestedFor(urlEqualTo(expectedUrl)));
    }

}