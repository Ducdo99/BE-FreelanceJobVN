package group5.freelancejob.repositories;

import group5.freelancejob.daos.Portfolio;
import group5.freelancejob.daos.Project;
import group5.freelancejob.daos.SkillProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findAllByNameLikeIgnoreCaseAndSkillProjectsIn(String name, List<SkillProject> skillProjects, Pageable pageable);
    Page<Project> findAllByNameLikeIgnoreCase(String name, Pageable pageable);
    Page<Project> findAllByNameLikeIgnoreCaseAndPortfolioPortfolioId(String name, Long portfolioId, Pageable pageable);
    @Query("select p from Project p where p.portfolio.freelancer.freelancerId = ?1")
    List<Project> findByPortfolio_Freelancer_FreelancerIdOrderByPrjIdAsc(Long freelancerId);
}
