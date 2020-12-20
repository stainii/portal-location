package be.stijnhooft.portal.location.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GeocodeResult implements Serializable {

    private String userInput;
    private String latitude;
    private String longitude;
    private String source;

}
