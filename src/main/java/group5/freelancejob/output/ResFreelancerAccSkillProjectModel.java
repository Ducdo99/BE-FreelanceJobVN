package group5.freelancejob.output;

import group5.freelancejob.models.FreelancerAccSkillDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResFreelancerAccSkillProjectModel extends FreelancerAccSkillDto {
    private List<ResProjectModel> projects;
}