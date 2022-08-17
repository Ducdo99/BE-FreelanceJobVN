package group5.freelancejob.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import group5.freelancejob.daos.*;
import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.mapper.*;
import group5.freelancejob.models.*;
import group5.freelancejob.mybatis.mapper.JobMyBatisMapper;
import group5.freelancejob.repositories.*;
import group5.freelancejob.services.MQTT.MqttService;
import group5.freelancejob.utils.*;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.spring.annotations.Recurring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobService {
    @Autowired
    JobRepository jobRepository;
    @Autowired
    JobSkillRepository jobSkillRepository;
    @Autowired
    RecruiterRepository recruiterRepository;
    @Autowired
    PaymentHistoryRepository paymentHistoryRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    SkillRepository skillRepository;
    @Autowired
    OfferRepository offerRepository;
    @Autowired
    GenreRepository genreRepository;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private SkillMapper skillMapper;
    @Autowired
    private OfferMapper offerMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private GenreMapper genreMapper;
    @Autowired
    private MqttService mqttService;
    @Autowired
    private JobMyBatisMapper jobMyBatisMapper;
    @Autowired
    private JobScheduler jobScheduler;

    public Object findAllJobs(int pageNo, int pageSize, List<Long> skillIds, JobStatus jobStatus, String jobTitle, Long genreId) throws FJVNException {
        if (pageNo <= 0) throw new FJVNException("Page number cannot lower than 1");
        pageNo -= 1;
        var queryResult = jobMyBatisMapper.
                selectJobFromTitleAndSkill(jobTitle, skillIds, jobStatus, genreId, pageNo * pageSize, pageSize);
        HashMap<String, Object> respObj = new LinkedHashMap<>();
        if (queryResult != null) {
            for (var job : queryResult) {
                job.setSkills(skillRepository.findByJobSkills_Job_JobId(job.getId()).stream().map(skill -> skill.convertToSkillDto()).collect(Collectors.toList()));
                job.setNoOfOffer((int) offerRepository.countByJob_JobId(job.getId()));
                job.setJobStatus(job.getJobStatusEnum().ordinal());

                var jobGenre = jobRepository.getById(job.getId()).getGenre();
                job.setGenre(genreMapper.convertToGenreDto(jobGenre));
            }

            respObj.put("data", queryResult);
        } else
            respObj.put("data", new ArrayList<>());
        var totalElements = jobMyBatisMapper
                .countSelectJobFromTitleAndSkill(jobTitle, skillIds, genreId, jobStatus);
        respObj.put("pageNo", pageNo + 1);
        respObj.put("pageSize", pageSize);
        respObj.put("totalCount", totalElements);
        respObj.put("totalPage", Math.ceil(totalElements.floatValue() / Integer.valueOf(pageSize).floatValue()));
        respObj.put("hasPreviousPage", pageNo + 1 > 1);
        respObj.put("hasNextPage", pageNo + 1 < Math.ceil(totalElements.floatValue() / Integer.valueOf(pageSize).floatValue()));
        return respObj;
    }

    private ResponseEntity<?> getResponseEntity(int pageNo, int pageSize, Page<Job> jobPage, HashMap<String, Object> respObj) {
        respObj.put("pageNo", pageNo);
        respObj.put("pageSize", pageSize);
        respObj.put("totalPage", jobPage.getTotalPages());
        respObj.put("totalCount", jobPage.getTotalElements());
        respObj.put("hasPreviousPage", pageNo > 1);
        respObj.put("hasNextPage", pageNo < jobPage.getTotalPages());

        return ResponseEntity.status(HttpStatus.OK).body(respObj);
    }

    public RespJobDto getJobById(Long jobId) {
        Job job = jobRepository.getById(jobId);
        List<Skill> jobSkills = job.getJobSkills().stream().map(JobSkill::getSkill).toList();

        RespJobDto resp = jobMapper.convertToRespJobDto(job);
        resp.setJobStatus(job.getJobStt().ordinal());
        resp.setJobStatusEnum(job.getJobStt());
        resp.setSkills(jobSkills.stream().map(e -> skillMapper.convertToSkillDto(e)).collect(Collectors.toList()));
        resp.setRecruiterName(job.getRecruiter().getFullname());
        resp.setRecruiterId(job.getRecruiter().getRecruiterId());
        resp.setOffers(job.getOffers().stream().map(e -> offerMapper.convertToOfferDto(e)).collect(Collectors.toList()));
        resp.setGenre(genreMapper.convertToGenreDto(job.getGenre()));
        //calculate avg offer
        Float avgOffers = 0F;
        Float highestOffer = CollectionUtils.isEmpty(job.getOffers()) ? 0F : job.getOffers().get(0).getOfferPrice();
        Float lowestOffer = CollectionUtils.isEmpty(job.getOffers()) ? 0F : job.getOffers().get(0).getOfferPrice();
        ;
        for (var offer :
                job.getOffers()) {
            avgOffers += offer.getOfferPrice();
            if (offer.getOfferPrice() > highestOffer) highestOffer = offer.getOfferPrice();
            if (offer.getOfferPrice() < lowestOffer) lowestOffer = offer.getOfferPrice();
        }
        if (job.getOffers().size() > 0) avgOffers = avgOffers / job.getOffers().size();
        else avgOffers = 0F;
        Map<String, Object> offerInfo = new HashMap<>();
        offerInfo.put("avg", avgOffers);
        offerInfo.put("highest", highestOffer);
        offerInfo.put("lowest", lowestOffer);
        resp.setOfferInfo(offerInfo);
        resp.setNoOfOffer(job.getOffers().size());
        return resp;
    }

    public List<RespJobDto> getJobByRecruiterId(Long recruiterId) throws FJVNException {
        var jobList = jobRepository.getJobsByRecruiter_RecruiterId(recruiterId);
        var resp = jobList.stream().map(e -> {
            var job = jobMapper.convertToRespJobDto(e);
            job.setSkills(skillRepository.findByJobSkills_Job_JobId(job.getId()).stream()
                    .map(skill -> skillMapper.convertToSkillDto(skill)).collect(Collectors.toList()));
            job.setRecruiterName(e.getRecruiter().getFullname());
            job.setOffers(e.getOffers().stream().map(offer -> offerMapper.convertToOfferDto(offer))
                    .collect(Collectors.toList()));
            return job;
        }).toList();
        return resp;
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class, Throwable.class})
    public ResponseEntity<?> createNewJob(NewJobReqDto reqJob) throws Exception {
        long jobId = 0;

        Job newJob = new Job();
        newJob.setTitle(reqJob.getTitle());
        newJob.setDescription(reqJob.getDescription());
        newJob.setDuration(reqJob.getDuration());
        newJob.setPrice(reqJob.getPrice());
        newJob.setRecruiter(recruiterRepository.getById(reqJob.getRecruiterId()));
        newJob.setJobStt(JobStatus.PUBLISHED);
        Date currentDate = new Date();
        newJob.setCreatedDate(currentDate);
        newJob.setModifiedDate(currentDate);
        newJob.setFreelancerRating(0);
        newJob.setRecruiterRating(0);

        var genre = genreRepository.getById(reqJob.getGenreId());
        newJob.setGenre(genre);

        jobId = jobRepository.save(newJob).getJobId();

        for (int i = 0; i < reqJob.getSkills().size(); i++) {
            JobSkill newJobSkill = new JobSkill();
            newJobSkill.setSkill(skillRepository.getById(reqJob.getSkills().get(i)));
            newJobSkill.setJob(jobRepository.getById(jobId));
            jobSkillRepository.save(newJobSkill);
        }

        HashMap resObj = new HashMap();
        resObj.put("id", jobId);

        return ResponseEntity.status(HttpStatus.CREATED).body(resObj);
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class, Throwable.class})
    public HttpStatus updateAJob(Long jobId, UpdateJobReqDto reqBody) throws Exception {
        String title = reqBody.getTitle();
        String description = reqBody.getDescription();
        try {
            Job updateJob = jobRepository.getById(jobId);
            updateJob.setTitle(title);
            updateJob.setDescription(description);
            updateJob.setDuration(reqBody.getDuration());
            updateJob.setPrice(reqBody.getPrice());
            Date currentDate = new Date();
            updateJob.setModifiedDate(currentDate);

            jobSkillRepository.deleteJobSkillsByJob_JobId(reqBody.getJobId());
            for (int i = 0; i < reqBody.getSkills().size(); i++) {
                JobSkill newJobSkill = new JobSkill();
                newJobSkill.setSkill(skillRepository.getById(reqBody.getSkills().get(i)));
                newJobSkill.setJob(jobRepository.getById(reqBody.getJobId()));
                jobSkillRepository.save(newJobSkill);
            }

            Genre genre = genreRepository.getById(reqBody.getGenreId());
            updateJob.setGenre(genre);

            jobRepository.save(updateJob);
            return HttpStatus.OK;
        } catch (Exception e) {
            e.printStackTrace();
            return HttpStatus.NOT_MODIFIED;
        }
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class, Throwable.class})
    public boolean requestToCompleteJob(Long jobId, RequestToCompleteJobDto request) throws Exception {
        Account theOtherChatterAccount;
        Job job = jobRepository.getById(jobId);
        if (job.getJobStt() == JobStatus.REQUEST_FOR_COMPLETE)
            throw new FJVNException("This job is already requested for complete.");
        Account requesterAccount = accountRepository.getById(request.getRequesterAccountId());
        if (request.getTheOtherChatterRecruiterId() != null) {
            var recruiter = recruiterRepository.getById(request.getTheOtherChatterRecruiterId());
            theOtherChatterAccount = recruiter.getAccount();
        } else
            theOtherChatterAccount = accountRepository.getById(request.getTheOtherChatterAccountId());
        if (requesterAccount == null)
            throw new FJVNException("Account with id " + request.getRequesterAccountId() + " not found.");
        if (theOtherChatterAccount == null)
            throw new FJVNException("Account with id " + request.getTheOtherChatterAccountId() + " not found.");
        if (job == null) throw new FJVNException("Job with id " + jobId + " not found.");
        //if requested to change job status to REQUEST_FOR_COMPLETE, create new message to display to
        //both recruiter and freelancer

        job.setJobStt(JobStatus.REQUEST_FOR_COMPLETE);
        Message message = new Message();
        message.setJob(job);
        message.setStatus(MessageStatus.SENT);
        String requesterFullName = requesterAccount.getFreelancer() == null ? requesterAccount.getRecruiter().getFullname() : requesterAccount.getFreelancer().getFullname();
        message.setContent(requesterFullName + " đã yêu cầu hoàn thành công việc.");
        message.setFromAccount(requesterAccount);
        message.setToAccount(theOtherChatterAccount);
        message.setSentTime(LocalDateTime.now());
        message.setMessageType(MessageType.NOTIFICATION);
        jobRepository.save(job);
        messageRepository.save(message);


        var msgResp = messageMapper.convertToMessageDto(message);

        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule(new JavaTimeModule());
        objMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        var topic = "/msg/" + theOtherChatterAccount.getId() + "/" + jobId;
        mqttService.publish(objMapper.writeValueAsString(msgResp), topic,
                MqttQos.AT_LEAST_ONCE, false);

        return true;
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class, Throwable.class})
    public boolean updateCompleteJob(Long jobId, UpdateCompleteJobDto request) throws Exception {
        // Only Recruiter can do this action
        Job job = jobRepository.getById(jobId);
        if (job.getJobStt() == JobStatus.DONE)
            throw new FJVNException("This job is already requested for complete.");
        Account requesterAccount = accountRepository.getById(request.getRequesterAccountId());

        var offer = offerRepository.getById(request.getOfferId());
        Account theOtherChatterAccount = offer.getFreelancer().getAccount();
        if (requesterAccount == null)
            throw new FJVNException("Account with id " + request.getRequesterAccountId() + " not found.");
        if (theOtherChatterAccount == null)
            throw new FJVNException("Freelancer not found.");
        if (job == null) throw new FJVNException("Job with id " + jobId + " not found.");
        if (offer == null) throw new FJVNException("Offer with id " + request.getOfferId() + " not found.");
        //if requested to change job status to REQUEST_FOR_COMPLETE, create new message to display to
        //both recruiter and freelancer

        var recruiterBalance = requesterAccount.getDeposit();
        var offerPrice = offer.getOfferPrice();

        if (recruiterBalance < offerPrice)
            throw new FJVNException("You don't have enough money to complete this job.");

        requesterAccount.setDeposit(recruiterBalance - offerPrice);
        theOtherChatterAccount.setDeposit(theOtherChatterAccount.getDeposit() + offerPrice);

        Random random = new Random();
        int random_id = random.nextInt(100000000);

        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setAmount((double) offerPrice);
        paymentHistory.setTransactionCode(String.valueOf(random_id));
        paymentHistory.setDescription("FVN - Thanh toán cho công việc #" + jobId + " - FL: " +
                theOtherChatterAccount.getFreelancer().getFullname());
        paymentHistory.setTransactionName(Constant.PAYMENT_TYPE_PAYMENT);
        paymentHistory.setStatus(PaymentHistoryStatus.SUCCESS);
        paymentHistory.setTransactionDate(LocalDateTime.now());

        paymentHistory.setAccount(requesterAccount);

        PaymentHistory flPaymentHistory = new PaymentHistory();
        flPaymentHistory.setAmount((double) offerPrice);
        flPaymentHistory.setTransactionCode(String.valueOf(random_id));
        flPaymentHistory.setDescription("FVN - Nhận thanh toán cho công việc #" + jobId + " - RC: " +
                requesterAccount.getRecruiter().getFullname());
        flPaymentHistory.setTransactionName(Constant.PAYMENT_TYPE_PAYMENT);
        flPaymentHistory.setStatus(PaymentHistoryStatus.SUCCESS);
        flPaymentHistory.setTransactionDate(LocalDateTime.now());

        flPaymentHistory.setAccount(theOtherChatterAccount);


        job.setJobStt(JobStatus.DONE);
        Message message = new Message();
        message.setJob(job);
        message.setStatus(MessageStatus.SENT);
        String requesterFullName = requesterAccount.getFreelancer() == null ? requesterAccount.getRecruiter().getFullname() : requesterAccount.getFreelancer().getFullname();
        message.setContent(requesterFullName + " đã xác nhận hoàn thành công việc.");
        message.setFromAccount(requesterAccount);
        message.setToAccount(theOtherChatterAccount);
        message.setSentTime(LocalDateTime.now());
        message.setMessageType(MessageType.NOTIFICATION);

        paymentHistoryRepository.save(paymentHistory);
        paymentHistoryRepository.save(flPaymentHistory);

        accountRepository.saveAll(Arrays.asList(requesterAccount, theOtherChatterAccount));
        jobRepository.save(job);
        messageRepository.save(message);

        var msgResp = messageMapper.convertToMessageDto(message);

        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule(new JavaTimeModule());
        objMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        var topic = "/msg/" + theOtherChatterAccount.getId() + "/" + jobId;
        mqttService.publish(objMapper.writeValueAsString(msgResp), topic,
                MqttQos.AT_LEAST_ONCE, false);

        return true;
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class, Throwable.class})
    public boolean requestToReopenJob(Long jobId, RequestToCompleteJobDto request) throws Exception {
        Job job = jobRepository.getById(jobId);
        if (job.getJobStt() != JobStatus.REQUEST_FOR_COMPLETE) throw new FJVNException("This job is already opened.");
        Account requesterAccount = accountRepository.getById(request.getRequesterAccountId());
        Account theOtherChatterAccount = accountRepository.getById(request.getTheOtherChatterAccountId());
        if (requesterAccount == null)
            throw new FJVNException("Account with id " + request.getRequesterAccountId() + " not found.");
        if (theOtherChatterAccount == null)
            throw new FJVNException("Account with id " + request.getTheOtherChatterAccountId() + " not found.");
        if (job == null) throw new FJVNException("Job with id " + jobId + " not found.");
        //if requested to change job status to REQUEST_FOR_COMPLETE, create new message to display to
        //both recruiter and freelancer

        job.setJobStt(JobStatus.ACCEPTED);
        Message message = new Message();
        message.setJob(job);
        message.setStatus(MessageStatus.SENT);
        String requesterFullName = requesterAccount.getFreelancer() == null ? requesterAccount.getRecruiter().getFullname() : requesterAccount.getFreelancer().getFullname();
        message.setContent(requesterFullName + " đã yêu cầu mở lại công việc.");
        message.setFromAccount(requesterAccount);
        message.setToAccount(theOtherChatterAccount);
        message.setSentTime(LocalDateTime.now());
        message.setMessageType(MessageType.NOTIFICATION);
        jobRepository.save(job);
        messageRepository.save(message);
        return true;
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class, Throwable.class})
    public ResponseEntity<?> banAJob(Long jobId) throws Exception {
        Job banJob = jobRepository.getById(jobId);
        banJob.setJobStt(JobStatus.BANNED);
        jobRepository.save(banJob);
        return ResponseEntity.noContent().build();
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class, Throwable.class})
    public void deleteJob(Long jobId) throws Exception {
        jobSkillRepository.deleteJobSkillsByJob_JobId(jobId);
        jobRepository.deleteById(jobId);
    }

    public Map<String, Long> getJobsPerMonth() throws Exception {
        // get current year and current month
        LocalDateTime currentDateTime = LocalDateTime.now();
        int currentYear = currentDateTime.getYear();
        int currentMonth = currentDateTime.getMonthValue();
        Long totalJob = jobRepository.countJobs(currentYear, currentMonth);
        Map<String, Long> mapObj = new HashMap<>();
        mapObj.put("jobsPerMonth", totalJob);
        return mapObj;
    }

    public Map<String, Integer> countFinishedJobs() throws Exception {
        int totalFinishedJob = jobRepository.countFinishedJobs();
        Map<String, Integer> mapObj = new HashMap<>();
        mapObj.put("totalFinishedJob", totalFinishedJob);
        return mapObj;
    }

    public Collection<RespJobDto> getTopTenJobs() throws Exception {
        var jobs = jobRepository.findTop10ByOrderByCreatedDateDesc();
        return jobs.stream().map(j -> {
            var dto = jobMapper.convertToRespJobDto(j);
            dto.setRecruiterName(j.getRecruiter().getFullname());
            dto.setJobStatus(j.getJobStt().ordinal());
            dto.setJobStatusEnum(j.getJobStt());
            dto.setRecruiterId(j.getRecruiter().getRecruiterId());
            return dto;
        }).collect(Collectors.toList());
    }

    public void runJobScheduler(){
        jobScheduler.scheduleRecurrently("check-expire","0 0 * * *", this::checkAndUpdateExpireJob);
    }

    @Recurring(id = "check-expire", cron = "0 0 * * *")
    @org.jobrunr.jobs.annotations.Job(name = "Daily check for expired jobs")
    @Transactional
    public void checkAndUpdateExpireJob() {
        var jobs = jobRepository.getJobsByDurationLessThanEqual(java.sql.Date.valueOf(LocalDate.now()));
        for (Job job : jobs) {
           if (job.getJobStt() == JobStatus.PUBLISHED){
               job.setJobStt(JobStatus.EXPIRED);
               var offers = job.getOffers();
               if (offers != null){
                   for (Offer offer : offers) {
                       offer.setStatus(OfferStatus.REJECTED);
                       offerRepository.save(offer);
                   }
               }
               jobRepository.save(job);
           }
        }
    }
}
