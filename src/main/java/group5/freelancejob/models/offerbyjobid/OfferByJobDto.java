package group5.freelancejob.models.offerbyjobid;

import group5.freelancejob.utils.OfferStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferByJobDto {
    private Long offerId;
    private Float offerPrice;
    private String timeToComplete;
    private String planning;
    private String experience;
    private String status;
    private FreelancerOfferByJobDto freelancer;
}
