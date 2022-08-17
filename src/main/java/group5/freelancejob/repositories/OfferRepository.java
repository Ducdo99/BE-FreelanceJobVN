package group5.freelancejob.repositories;

import group5.freelancejob.daos.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    @Query("select count(o) from Offer o where o.job.jobId = ?1")
    long countByJob_JobId(Long jobId);

    List<Offer> getOffersByFreelancer_FreelancerId(Long freelancerId);

    Offer getOfferByFreelancer_FreelancerIdAndJob_JobId(Long freelancerId, Long jobId);

    @Query(nativeQuery = true,
            value = "SELECT COUNT(o.id) FROM Offer o")
    Long getTotalOffers();

    @Query(nativeQuery = true,
            value = "SELECT COUNT(o.id) FROM Offer o WHERE o.status = 'REJECTED'")
    Long getTotalRejectedOffers();
}
