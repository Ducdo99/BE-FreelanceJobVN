package group5.freelancejob.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountDto {
    private Long accId;
    private String username;
    private String pwd;
    private String phone;
    private String email;
    private float deposit;
    private String avatar;
    private FreelancerDto freelancer;
    private RecruiterDto recruiter;
}
