package group5.freelancejob.models.offerbyjobid;


import group5.freelancejob.models.SkillDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FreelancerOfferByJobDto {
    private Long freelancerId;
    private String fullname;
    private String phone;
    private String email;
    private String rating;
    private String shortDescription;
    private List<SkillDto> skills; //you have to get freelancer skills by freelancer API
    private String avatar;
}