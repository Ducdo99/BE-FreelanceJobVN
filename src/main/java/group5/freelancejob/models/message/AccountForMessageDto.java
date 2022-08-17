package group5.freelancejob.models.message;

import group5.freelancejob.models.FreelancerDto;
import group5.freelancejob.models.RecruiterDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountForMessageDto {
    private Long accId;
    private String username;
    private String pwd;
    private String phone;
    private String email;
    private float deposit;
    private String avatar;
    private FreelancerDto freelancer;
    private RecruiterForMessageDto recruiter;
}
