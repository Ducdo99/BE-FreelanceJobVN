package group5.freelancejob.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FreelancerDto {
    @JsonProperty("id")
    private Long freelancerId;
    private String fullname;
    private String rating;
    private String roleAtWork;
    private String shortDescription;
    private String description;
}