package group5.freelancejob.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FreelancerAccSkillDto {
    private Long id;
    private String name;
    private String avatar;
    private String rating;
    private String phone;
    private String email;
    private String roleAtWork;
    private String shortDescription;
    private String description;
    private List<SkillDto> skills;
}
