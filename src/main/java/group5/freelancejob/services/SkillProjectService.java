package group5.freelancejob.services;

import group5.freelancejob.daos.Project;
import group5.freelancejob.daos.Skill;
import group5.freelancejob.daos.SkillProject;
import group5.freelancejob.repositories.SkillProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SkillProjectService {

    @Autowired
    SkillProjectRepository skillProjectRepository;

    @Transactional(rollbackFor = {SQLException.class, Exception.class, Throwable.class})
    public void insertSkillProject(List<Long> skillIdList, Project projectEntity) throws Exception {
        List<SkillProject> skillProjectEntities = new ArrayList<>();
        for (Long skillId : skillIdList
        ) {
            Skill skillEntity = new Skill();
            skillEntity.setSkillId(skillId);
            SkillProject skillProjectEntity = new SkillProject();
            skillProjectEntity.setProject(projectEntity);
            skillProjectEntity.setSkill(skillEntity);
            skillProjectEntities.add(skillProjectRepository.save(skillProjectEntity));
        }
    }
}
