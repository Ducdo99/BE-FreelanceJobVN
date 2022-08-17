package group5.freelancejob.daos;

import group5.freelancejob.models.FreelancerAccSkillDto;
import group5.freelancejob.models.FreelancerDto;
import group5.freelancejob.output.ResFreelancerAccSkillProjectModel;
import group5.freelancejob.utils.Constant;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Freelancer")
public class Freelancer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long freelancerId;
    @Column(name = "fullname")
    private String fullname;
    @Column(name = "rating")
    private String rating;
    @Column(name = "role_at_work")
    private String roleAtWork;
    @Column(name = "short_description")
    private String shortDescription;
    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @OneToOne(mappedBy = "freelancer")
    private Portfolio portfolio;

    @OneToMany(mappedBy = "freelancer")
    private List<Job> jobs;
    @OneToMany(mappedBy = "freelancer")
    private List<Offer> offers;

    public FreelancerDto convertToFreelancerDto() {
        FreelancerDto dto = new FreelancerDto();
        dto.setFreelancerId(freelancerId);
        dto.setFullname(fullname);
        dto.setShortDescription(shortDescription);
        dto.setDescription(description);
        dto.setRoleAtWork(roleAtWork);
        dto.setRating(Constant.calculateRating(rating));
        return dto;
    }

    public FreelancerAccSkillDto convertToFreelancerAccSkillDto() {
        FreelancerAccSkillDto dto = new FreelancerAccSkillDto();
        dto.setId(freelancerId);
        dto.setName(fullname);
        dto.setAvatar(account.getAvatar());
        dto.setRating(Constant.calculateRating(rating));
        dto.setPhone(account.getPhone());
        dto.setEmail(account.getEmail());
        dto.setRoleAtWork(roleAtWork);
        dto.setShortDescription(shortDescription);
        dto.setDescription(description);
        return dto;
    }

    public ResFreelancerAccSkillProjectModel convertToResFreelancerAccSkillProjectModel() {
        ResFreelancerAccSkillProjectModel dto = new ResFreelancerAccSkillProjectModel();
        dto.setId(freelancerId);
        dto.setName(fullname);
        dto.setAvatar(account.getAvatar());
        dto.setRating(Constant.calculateRating(rating));
        dto.setPhone(account.getPhone());
        dto.setEmail(account.getEmail());
        dto.setRoleAtWork(roleAtWork);
        dto.setShortDescription(shortDescription);
        dto.setDescription(description);
        return dto;
    }
}
