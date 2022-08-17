package group5.freelancejob.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PortfolioDto {
    private Long portfolioId;
    private String description;
    private String avatar;
    private String phone;
    private FreelancerDto freelancer;
}
