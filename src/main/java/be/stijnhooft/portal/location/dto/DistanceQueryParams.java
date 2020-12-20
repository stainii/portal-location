package be.stijnhooft.portal.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DistanceQueryParams implements Serializable {

    private String location1Query;
    private String location2Query;

}
