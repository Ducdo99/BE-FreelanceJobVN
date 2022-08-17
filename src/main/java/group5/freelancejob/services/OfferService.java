package group5.freelancejob.services;

import group5.freelancejob.daos.Job;
import group5.freelancejob.daos.Message;
import group5.freelancejob.daos.Offer;
import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.mapper.JobMapper;
import group5.freelancejob.mapper.OfferMapper;
import group5.freelancejob.models.CreateOfferRequest;
import group5.freelancejob.models.OfferDto;
import group5.freelancejob.models.OfferWithJobDto;
import group5.freelancejob.models.SkillDto;
import group5.freelancejob.models.offerbyjobid.OfferByJobDto;
import group5.freelancejob.models.offerbyjobid.OfferByJobIdDto;
import group5.freelancejob.repositories.*;
import group5.freelancejob.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class OfferService {
    @Autowired
    OfferRepository offerRepository;
    @Autowired
    JobRepository jobRepository;
    @Autowired
    FreelancerRepository freelancerRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private OfferMapper offerMapper;

    public OfferByJobIdDto getOffersByJobId(Long jobId) throws FJVNException {
        Job job = jobRepository.getById(jobId);
        if (job == null) throw new FJVNException("Job with id " + jobId + " not exist.");
        OfferByJobIdDto resp = jobMapper.convertToOfferByJobIdDto(job);
        List<Offer> offers = job.getOffers();

        //calculate avg offer
        Float avgOffers = 0F;
        Float highestOffer = CollectionUtils.isEmpty(offers) ? 0F : offers.get(0).getOfferPrice();
        Float lowestOffer = CollectionUtils.isEmpty(offers) ? 0F : offers.get(0).getOfferPrice();

        for (var offer : offers) {
            avgOffers += offer.getOfferPrice();
            if (offer.getOfferPrice() > highestOffer) highestOffer = offer.getOfferPrice();
            if (offer.getOfferPrice() < lowestOffer) lowestOffer = offer.getOfferPrice();
        }

        for (var freelancer : resp.getOffers().stream().map(OfferByJobDto::getFreelancer).collect(Collectors.toList())) {
            if (freelancer != null) {
                List<SkillDto> skillDtos = skillRepository.findByFreelancerId(freelancer.getFreelancerId()).stream().map(skill -> skill.convertToSkillDto()).collect(Collectors.toList());
                freelancer.setSkills(skillDtos);
            }
        }

        if (offers.size() > 0) avgOffers = avgOffers / offers.size();
        else avgOffers = 0F;
        Map<String, Object> offerInfo = new HashMap<>();
        offerInfo.put("avg", avgOffers);
        offerInfo.put("highest", highestOffer);
        offerInfo.put("lowest", lowestOffer);
        resp.setOfferInfo(offerInfo);
        return resp;
    }

    public List<OfferWithJobDto> getOffersByFreelancerId(Long freelancerId) throws FJVNException {
        List<Offer> offers = offerRepository.getOffersByFreelancer_FreelancerId(freelancerId);
        if (CollectionUtils.isEmpty(offers))
            throw new FJVNException("No offer found for freelancer with id " + freelancerId);
        return offers.stream().map(offerMapper::convertToOfferWithJobDto).collect(java.util.stream.Collectors.toList());
    }

    public OfferDto getOfferByFreelancerIdAndJobId(Long freelancerId, Long jobId) throws FJVNException {
        Offer offer = offerRepository.getOfferByFreelancer_FreelancerIdAndJob_JobId(freelancerId, jobId);
        if (offer == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No offer found for freelancer with id " + freelancerId + " and job with id " + jobId);
        return offerMapper.convertToOfferDto(offer);
    }

    @Transactional(rollbackFor = {Exception.class, SQLException.class, Throwable.class})
    public void updateOfferStatus(Long offerId, Long jobId, OfferStatus status) throws Exception {
        Offer offer = offerRepository.getById(offerId);
        Job job = jobRepository.getById(jobId);
        if (offer == null || job == null) {
            throw new Exception("Offer or job not found");
        }

        if (job.getJobStt() == JobStatus.EXPIRED) {
            throw new FJVNException("Job is expired");
        }
        var listOfferFromJob = job.getOffers();
        // Change status of all offers in job to REJECTED first
        for (var offerFromJob : listOfferFromJob) {
            offerFromJob.setStatus(OfferStatus.REJECTED);
        }
        offer.setJob(job);

        // Then change status of chosen offer to ACCEPTED
        offer.setStatus(status);
        //change job status to ACCEPTED because an offer is accepted
        job.setJobStt(JobStatus.ACCEPTED);

        Message message = new Message();
        message.setJob(job);
        message.setStatus(MessageStatus.SENT);
        String requesterFullName = job.getRecruiter().getFullname();
        message.setContent(requesterFullName + " đã chấp nhận chào giá của bạn.");
        message.setFromAccount(job.getRecruiter().getAccount());
        message.setToAccount(offer.getFreelancer().getAccount());
        message.setSentTime(LocalDateTime.now());
        message.setMessageType(MessageType.NOTIFICATION);

        messageRepository.save(message);

        offerRepository.save(offer);
        jobRepository.save(job);
    }


    public Map<String, Long> updateOffer(Long offerId, OfferDto offerDto) throws Exception {
        Offer updateOffer = offerRepository.getById(offerId);

        updateOffer.setOfferPrice(offerDto.getOfferPrice());
        updateOffer.setExperience(offerDto.getExperience());
        updateOffer.setTimeToComplete(offerDto.getTimeToComplete());
        updateOffer.setPlanning(offerDto.getPlanning());
        Long id = offerRepository.save(updateOffer).getJob().getJobId();

        Map<String, Long> mapObj = new LinkedHashMap<>();
        mapObj.put("jobId", id);

        return mapObj;
    }

    public void deleteOffer(Long offerId) throws NumberFormatException, Exception {
        offerRepository.deleteById(offerId);
    }

    public Long createNewOffer(Long jobId, CreateOfferRequest reqBody) throws Exception {
        float offerPrice = Float.parseFloat(reqBody.getOfferPrice());
        String timeToComplete = reqBody.getTimeToComplete();
        String experience = reqBody.getExperience();
        String planning = reqBody.getPlanning();
        Long freelancerId = reqBody.getFreelancerId();

        var freelancer = freelancerRepository.getById(freelancerId);
        var accountOfFreelancer = freelancer.getAccount();

        if (accountOfFreelancer.getDeposit() < Constant.MINIMUM_DEPOSIT) {
            throw new FJVNException(Constant.MINIMUM_DEPOSIT_TO_OFFER_MESSAGE);
        }

        Job aJob = jobRepository.getById(jobId);
        Offer newOffer = new Offer();
        newOffer.setOfferPrice(offerPrice);
        newOffer.setTimeToComplete(timeToComplete);
        newOffer.setExperience(experience);
        newOffer.setPlanning(planning);
        newOffer.setJob(aJob);
        newOffer.setStatus(OfferStatus.OFFERING);
        newOffer.setFreelancer(freelancer);

        try {
            return offerRepository.saveAndFlush(newOffer).getOfferId();
        } catch (Exception e) {
            throw new FJVNException("Freelancer đã chào giá việc này");
        }
    }

    public ResponseEntity<?> getTotalOffers() throws Exception {
        Long totalOffers = offerRepository.getTotalOffers();
        return ResponseEntity.ok().body(Collections.singletonMap("totalOffers", totalOffers));
    }

    public ResponseEntity<?> getTotalRejectedOffers() throws Exception {
        Long totalRejectedOffers = offerRepository.getTotalRejectedOffers();
        return ResponseEntity.ok().body(Collections.singletonMap("totalRejectedOffers", totalRejectedOffers));
    }
}