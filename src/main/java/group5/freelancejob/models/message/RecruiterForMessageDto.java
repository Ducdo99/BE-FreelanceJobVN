package group5.freelancejob.models.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import group5.freelancejob.models.JobDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RecruiterForMessageDto {
    @JsonProperty("id")
    private Long recruiterId;
    private String fullname;
    private String shortDescription;
    private String description;
    private String avatar;
    private String rating;
}
