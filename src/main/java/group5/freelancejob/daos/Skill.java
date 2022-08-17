package group5.freelancejob.daos;

import group5.freelancejob.models.SkillDto;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Skill")
public class Skill implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long skillId;
    @Column(name = "name")
    private String skillName;

    @OneToMany(mappedBy = "skill")
    private List<SkillProject> skillProjects;
    @OneToMany(mappedBy = "skill")
    private List<JobSkill> jobSkills;

    public SkillDto convertToSkillDto() {
        SkillDto dto = new SkillDto();
        dto.setSkillId(skillId);
        dto.setSkillName(skillName);
        return dto;
    }

    public List<Skill> convertDTOToEntity(List<SkillDto> dtos) {
        List<Skill> entities = new ArrayList<>();
        for (SkillDto dto : dtos
        ) {
            Skill entity = new Skill();
            entity.setSkillName(dto.getSkillName());
            entity.setSkillId(dto.getSkillId());
            entities.add(entity);
        }
        return entities;
    }

    public List<SkillDto> convertEntityToDTO(List<Skill> entities) {
        List<SkillDto> skillDtos = null;
        if (skillDtos == null) {
            skillDtos = new ArrayList<>();
        }
        for (Skill entity : entities
        ) {
            SkillDto skillDto = new SkillDto(entity.getSkillId(), entity.getSkillName());
            skillDtos.add(skillDto);
        }
        return skillDtos;
    }
}