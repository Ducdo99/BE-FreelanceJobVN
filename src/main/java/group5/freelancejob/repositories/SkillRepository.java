package group5.freelancejob.repositories;

import group5.freelancejob.daos.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    @Query(nativeQuery = true,
            value = "SELECT *\n" +
                    "FROM Skill AS S\n" +
                    "JOIN Skill_Project SP on S.id = SP.skill_id\n" +
                    "JOIN Project PRJ on SP.project_id = PRJ.id\n" +
                    "JOIN Portfolio P on PRJ.portfolio_id = P.id\n" +
                    "JOIN Freelancer F on P.freelancer_id = F.id\n" +
                    "WHERE F.id = :freelancerId")
    List<Skill> findByFreelancerId(@Param(value = "freelancerId") Long id);

    @Query("select s from Skill s inner join s.jobSkills jobSkills where jobSkills.job.jobId = ?1")
    List<Skill> findByJobSkills_Job_JobId(Long jobId);

    @Query(value = "select s " +
            "from Skill s")
    List<Skill> findAll();

}