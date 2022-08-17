package group5.freelancejob.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqUpdatePortfolioDto {
    private String portfolioDescription;
    private String fullName;
    private String description;
    private String shortDescription;
    private String avatar;
    private String phone;
    private String role;
}
