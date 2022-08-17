package group5.freelancejob.repositories;

import group5.freelancejob.daos.Account;
import group5.freelancejob.daos.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("select a from Account a where a.freelancer.freelancerId = ?1")
    Account findByFreelancer_FreelancerId(Long freelancerId);

    Account findFirstByEmail(String email);

    Account findFirstByFreelancer_FreelancerId(Long freelancerId);

    @Query("""
            Select a from Account a left join a.recruiter left join a.freelancer
                """)
    Collection<Account> getAll();

}