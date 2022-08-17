package group5.freelancejob.repositories;

import group5.freelancejob.daos.Project;
import group5.freelancejob.daos.Skill;
import group5.freelancejob.daos.SkillProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillProjectRepository extends JpaRepository<SkillProject, Long> {
    @Query(value = "select sp " +
            "from SkillProject sp " +
            "where sp.project.prjId = :id")
    List<SkillProject> findAllByProjectId(@Param(value = "id") Long id);
    List<SkillProject> findAllBySkillIn(List<Skill> skills);
    long deleteAllByProject(Project project);
}
