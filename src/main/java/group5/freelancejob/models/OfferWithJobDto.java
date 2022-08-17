package group5.freelancejob.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferWithJobDto {
    private Long offerId;
    private float offerPrice;
    private String timeToComplete;
    private String status;
    private String jobStatus;
    private String jobTitle;
    private Long jobId;
    private Integer freelancerRating;
    private Integer recruiterRating;
}
