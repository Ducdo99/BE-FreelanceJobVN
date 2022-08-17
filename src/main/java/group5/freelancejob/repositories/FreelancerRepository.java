package group5.freelancejob.repositories;

import group5.freelancejob.daos.Freelancer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, Long> {
    @Query(nativeQuery = true,
            countQuery = "SELECT COUNT(FL.id)\n" +
                    "FROM Freelancer AS FL\n" +
                    "         JOIN Portfolio P on FL.id = P.freelancer_id\n" +
                    "         JOIN Project P2 on P.id = P2.portfolio_id\n" +
                    "         JOIN Skill_Project SP on P2.id = SP.project_id\n" +
                    "         JOIN Skill S on SP.skill_id = S.id\n" +
                    "WHERE FL.fullname LIKE '%' + :fullname + '%'\n" +
                    "  AND S.id IN (:skillIds)",
            value = "SELECT DISTINCT FL.id, FL.fullname, FL.rating, Fl.short_description, FL.description, FL.account_id, FL.role_at_work \n" +
                    "FROM Freelancer AS FL\n" +
                    "         JOIN Portfolio P on FL.id = P.freelancer_id\n" +
                    "         JOIN Project P2 on P.id = P2.portfolio_id\n" +
                    "         JOIN Skill_Project SP on P2.id = SP.project_id\n" +
                    "         JOIN Skill S on SP.skill_id = S.id\n" +
                    "WHERE FL.fullname LIKE '%' + :fullname + '%'\n" +
                    "  AND S.id IN (:skillIds)")
    Page<Freelancer> findFreelancersByFullnameContainsAndSkillsIn(@RequestParam(value = "fullname") String fullname,
                                                                  @RequestParam(value = "skillIds") List<Long> skillIds,
                                                                  Pageable pageable);

    Page<Freelancer> findAllByFullnameLike(String fullName, Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT DISTINCT FL.id, FL.fullname, FL.rating, FL.short_description, FL.description, FL.account_id, FL.role_at_work \n" +
                    "FROM Freelancer AS FL\n" +
                    "         LEFT JOIN Portfolio P on FL.id = P.freelancer_id\n" +
                    "         LEFT JOIN Project P2 on P.id = P2.portfolio_id\n" +
                    "         LEFT JOIN Skill_Project SP on P2.id = SP.project_id\n" +
                    "         LEFT JOIN Skill S on SP.skill_id = S.id\n" +
                    "WHERE FL.id = :freelancerId")
    Freelancer findFreelancerAndSkillByFreelancerId(@RequestParam(value = "freelancerId") Long freelancerId);
}