package group5.freelancejob.repositories;

import group5.freelancejob.daos.JobSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkill, Long> {
    void deleteJobSkillsByJob_JobId(Long jobId);
}
