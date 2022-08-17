package group5.freelancejob.services;

import group5.freelancejob.daos.Skill;
import group5.freelancejob.daos.SkillProject;
import group5.freelancejob.models.SkillDto;
import group5.freelancejob.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {
    @Autowired
    SkillRepository skillRepository;

    public List<SkillDto> getSkills() {
        return skillRepository.findAll().stream().map(skill -> skill.convertToSkillDto()).collect(Collectors.toList());
    }

    public List<SkillDto> getSkillsBySkillIdList(List<SkillProject> skillProjects) {
        List<SkillDto> skillDtos = new ArrayList<>();
        for (SkillProject entity : skillProjects) {
            Long skillId = entity.getSkill().getSkillId();
            Skill skillEntity = skillRepository.getById(skillId);
            SkillDto skillDto = skillEntity.convertToSkillDto();
            skillDtos.add(skillDto);
        }
        return skillDtos;
    }


    public List<SkillDto> getSkillDetailList() {
        List<Skill> skillEntities = skillRepository.findAll();
        Skill skillEntity = new Skill();
        return skillEntity.convertEntityToDTO(skillEntities);
    }
}
