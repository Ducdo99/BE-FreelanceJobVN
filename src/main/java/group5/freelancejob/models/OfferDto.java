package group5.freelancejob.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OfferDto {
    private Long offerId;
    private float offerPrice;
    private String timeToComplete;
    private String experience;
    private String planning;
    private String status;
}
