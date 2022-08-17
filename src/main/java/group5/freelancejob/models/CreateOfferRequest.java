package group5.freelancejob.models;

import lombok.Getter;

@Getter
public class CreateOfferRequest{
    private Long freelancerId;
    private String offerPrice;
    private String timeToComplete;
    private String experience;
    private String planning;
}
