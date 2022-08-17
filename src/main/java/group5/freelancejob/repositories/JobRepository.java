package group5.freelancejob.repositories;

import group5.freelancejob.daos.Job;
import group5.freelancejob.utils.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    @Query("""
            select distinct j from Job j left join j.jobSkills jobSkills 
            where jobSkills.skill.skillName like concat('%', ?1, '%') 
            or j.title like concat('%', ?2, '%') 
            and j.jobStt = ?3
            """)
    Page<Job> findDistinctByJobSkills_Skill_SkillNameLikeOrTitleContainsAndJobStt(String skillName, String title, JobStatus jobStt, Pageable pageable);

    @Query("""
            select distinct j from Job j left join j.jobSkills jobSkills 
            where jobSkills.skill.skillName like concat('%', ?1, '%') 
            or j.title like concat('%', ?2, '%')
            """)
    Page<Job> findDistinctByJobSkills_Skill_SkillNameLikeOrTitleContains(String skillName, String title, Pageable pageable);

    Collection<Job> getJobsByRecruiter_RecruiterId(Long recruiterId);

    Collection<Job> findTop10ByOrderByCreatedDateDesc();

    Collection<Job> getJobsByDurationLessThanEqual(Date duration);

    @Query("select count(job.jobId) " +
            "from Job job " +
            "where year(job.duration) = ?1 and month(job.duration) = ?2")
    Long countJobs(int currentYear, int currentMonth);

    @Query(value = "select count(job.jobId) from Job  job where job.jobStt = 'DONE'")
    int countFinishedJobs();
}