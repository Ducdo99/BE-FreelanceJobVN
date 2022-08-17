package group5.freelancejob.repositories;

import group5.freelancejob.daos.Recruiter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {
    Page<Recruiter> findAllByFullnameLike(String name, Pageable pageable);
}