package group5.freelancejob.repositories;

import group5.freelancejob.daos.Job;
import group5.freelancejob.daos.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> getMessagesByFromAccountIdOrToAccountIdOrderBySentTimeAsc(Long fromAccountId, Long toAccountId);
    List<Message> getMessagesByFromAccountIdAndToAccountIdAndJobOrderBySentTimeAsc(Long fromAccountId, Long toAccountId, Job job);
    List<Message> getMessagesByJob_JobIdOrderBySentTimeAsc(Long jobId);
    Message getFirstByFromAccountIdOrToAccountIdOrderBySentTimeAsc(Long fromAccountId, Long toAccountId);
}
