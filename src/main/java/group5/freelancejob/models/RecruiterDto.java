package group5.freelancejob.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RecruiterDto {
    @JsonProperty("id")
    private Long recruiterId;
    private String fullname;
    private String shortDescription;
    private String description;
    private String avatar;
    private String email;
    private String phone;
    private List<JobDto> jobs;
    private String rating;
}
