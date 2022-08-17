package group5.freelancejob.output;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseFreelancerObject {
    private Long freelancerId;
    private String freelancerName;
    private String imageUrl;
    private String shortDescription;
}
