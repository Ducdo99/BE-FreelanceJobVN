package group5.freelancejob.models;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestCreateNewProject extends ProjectDto implements Serializable {
    private Long freelancerId;
    private List<Long> skillIds;
}