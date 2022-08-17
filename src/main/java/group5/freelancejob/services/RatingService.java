package group5.freelancejob.services;

import group5.freelancejob.daos.Account;
import group5.freelancejob.daos.Job;
import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.mapper.JobMapper;
import group5.freelancejob.models.JobDto;
import group5.freelancejob.models.RequestRateJobDto;
import group5.freelancejob.models.RespJobDto;
import group5.freelancejob.repositories.AccountRepository;
import group5.freelancejob.repositories.JobRepository;
import group5.freelancejob.utils.JobStatus;
import group5.freelancejob.utils.OfferStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingService {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JobMapper jobMapper;

    public JobDto rateJob(Long jobId, RequestRateJobDto requestRateJobDto) throws FJVNException {
        Job job = jobRepository.getById(jobId);
        if (job == null) throw new FJVNException("Job with id " + jobId + " is not found.");
        if (job.getJobStt() != JobStatus.DONE)
            throw new FJVNException("Job with id " + jobId + " cannot be rated because it is not finished.");
        Account account = accountRepository.getById(requestRateJobDto.getAccountId());
        if (account == null)
            throw new FJVNException("Account with id " + requestRateJobDto.getAccountId() + " is not found.");
        if (account.getFreelancer() != null) {
            var acceptedOffer = job.getOffers().stream().filter(e -> e.getStatus() == OfferStatus.ACCEPTED).findFirst();
            if (acceptedOffer == null || !acceptedOffer.isPresent()) throw new FJVNException("This job has no valid offer (with status ACCEPTED).");
            if (account.getFreelancer().getFreelancerId() == acceptedOffer.get().getFreelancer().getFreelancerId()) {
                job.setFreelancerRating(requestRateJobDto.getRating());
            } else {
                throw new FJVNException("Current freelancer with id: " + account.getFreelancer().getFreelancerId() + " cannot rate this job.");
            }
        } else if (account.getRecruiter() != null) {
            if (job.getRecruiter().getRecruiterId() == account.getRecruiter().getRecruiterId())
            job.setRecruiterRating(requestRateJobDto.getRating());
            else throw new FJVNException("Current recruiter with id: " + account.getRecruiter().getRecruiterId() + " cannot rate this job.");
        }
        jobRepository.save(job);
        return jobMapper.convertToJobDto(job);
    }
}
