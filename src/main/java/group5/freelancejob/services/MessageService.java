package group5.freelancejob.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import group5.freelancejob.daos.Account;
import group5.freelancejob.daos.Job;
import group5.freelancejob.daos.Message;
import group5.freelancejob.daos.Offer;
import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.mapper.MessageMapper;
import group5.freelancejob.mapper.OfferMapper;
import group5.freelancejob.models.CreateMessageRequest;
import group5.freelancejob.models.OfferDto;
import group5.freelancejob.models.message.GetMessageRespDto;
import group5.freelancejob.models.message.MessageDto;
import group5.freelancejob.models.message.MessageDto2;
import group5.freelancejob.repositories.*;
import group5.freelancejob.services.MQTT.MqttService;
import group5.freelancejob.utils.MessageStatus;
import group5.freelancejob.utils.MessageType;
import group5.freelancejob.utils.OfferStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private FreelancerRepository freelancerRepository;
    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private OfferMapper offerMapper;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MqttService mqttService;

    public List<MessageDto> getAllMessageByAccountId(Long accountId) throws FJVNException {
        var account = accountRepository.getById(accountId);

        if (account == null) {
            throw new FJVNException("Account not found!");
        }
        List<Message> msgs = messageRepository.getMessagesByFromAccountIdOrToAccountIdOrderBySentTimeAsc(accountId, accountId);
        List<MessageDto> resp = msgs.stream().map(messageMapper::convertToMessageDto).collect(java.util.stream.Collectors.toList());

        return getOfferForMsg(resp);
    }

    public List<MessageDto2> getMessagesWithOneAccountInJob(Long originalAccountId, Long targetAccountId, Long jobId) throws FJVNException {
        var originalAccount = accountRepository.getById(originalAccountId);
        var targetAccount = accountRepository.getById(targetAccountId);
        var job = jobRepository.getById(jobId);
        if (originalAccount == null || targetAccount == null || job == null)
            throw new FJVNException("Cannot find Accounts or Job");
        List<Message> messages = messageRepository.getMessagesByFromAccountIdAndToAccountIdAndJobOrderBySentTimeAsc(originalAccountId, targetAccountId, job);
        messages.addAll(messageRepository.getMessagesByFromAccountIdAndToAccountIdAndJobOrderBySentTimeAsc(targetAccountId, originalAccountId, job));
        Collections.sort(messages, Comparator.comparing(Message::getSentTime));

        //if from account's freelancer is not null, then from account is freelancer
        //and to account is recruiter
        //and vice versa
        Offer offer = null;
        if (originalAccount.getFreelancer() != null) {
            offer = offerRepository.getOfferByFreelancer_FreelancerIdAndJob_JobId(originalAccount.getFreelancer().getFreelancerId(), jobId);
        } else {
            offer = offerRepository.getOfferByFreelancer_FreelancerIdAndJob_JobId(targetAccount.getFreelancer().getFreelancerId(), jobId);
        }
        if (offer == null)
            throw new FJVNException("The current freelancer's offer for this job is invalid! Please report to DB Admin to fix this error.");
        return messages.stream().map(messageMapper::convertToMessageDto2).collect(Collectors.toList());
    }

    private List<MessageDto> getOfferForMsg(List<MessageDto> resp) throws FJVNException {
        for (var message :
                resp) {
            //if from account's freelancer is not null, then from account is freelancer
            //and to account is recruiter
            //and vice versa
            Offer offer = null;
            if (message.getFromAccount().getFreelancer() != null) {
                offer = offerRepository.getOfferByFreelancer_FreelancerIdAndJob_JobId(message.getFromAccount().getFreelancer().getFreelancerId(), message.getJob().getJobId());
            } else {
                offer = offerRepository.getOfferByFreelancer_FreelancerIdAndJob_JobId(message.getToAccount().getFreelancer().getFreelancerId(), message.getJob().getJobId());
            }
            if (offer == null)
                throw new FJVNException("The current freelancer's offer for this job is invalid! Please report to DB Admin to fix this error.");
            message.setCurrentOffer(offerMapper.convertToOfferDto(offer));
        }

        return resp;
    }

    public Set<GetMessageRespDto> getAllMessageByAccountIdAndJobId(Long accountId, Long jobId) throws FJVNException {
        var account = accountRepository.getById(accountId);
        Job job = jobRepository.getById(jobId);
        if (account == null) {
            throw new FJVNException("Account not found!");
        }
        if (job == null) throw new FJVNException("Job not found!");

        List<Message> msgs = messageRepository.getMessagesByFromAccountIdOrToAccountIdOrderBySentTimeAsc(accountId, accountId);
        msgs = msgs.stream().filter(msg -> msg.getJob().getJobId() == jobId).distinct().toList();
        Set<GetMessageRespDto> respList = msgs.stream().map(e -> messageMapper.populateWithTargetAccount(e, account)).distinct().collect(Collectors.toSet());
        for (var resp :
                respList) {
            List<Message> matchedMessages =
                    msgs.stream().filter(
                                    e ->
                                            resp.getTargetUser().getAccId() == e.getToAccount().getId() || resp.getTargetUser().getAccId() == e.getFromAccount().getId())
                            .toList();
            resp.setMessages(matchedMessages.stream().map(messageMapper::convertToMessageDto2).collect(Collectors.toList()));
        }

        for (var resp :
                respList) {
            //if from account's freelancer is not null, then from account is freelancer
            //and to account is recruiter
            //and vice versa
            Offer offer = null;
            if (account.getFreelancer() != null) {
                offer = offerRepository.getOfferByFreelancer_FreelancerIdAndJob_JobId(account.getFreelancer().getFreelancerId(), jobId);
            } else {
                offer = offerRepository.getOfferByFreelancer_FreelancerIdAndJob_JobId(resp.getTargetUser().getFreelancer().getFreelancerId(), jobId);
            }
            if (offer == null)
                throw new FJVNException("The current freelancer's offer for this job is invalid! Please report to DB Admin to fix this error.");
            resp.setCurrentOffer(offerMapper.convertToOfferDto(offer));
        }

        return respList;
    }

    @Transactional(rollbackFor = {SQLException.class, FJVNException.class, Throwable.class})
    public MessageDto createMessage(CreateMessageRequest messageDto) throws FJVNException, JsonProcessingException {
        Account toAccount;
        var fromAccount = accountRepository.getById(messageDto.getFromAccountId());
        if (messageDto.getToFreelanceId() != null) {
            var toFreelance = freelancerRepository.getById(messageDto.getToFreelanceId());
            if (toFreelance == null) throw new FJVNException("ToFreelance is not valid!");
            toAccount = toFreelance.getAccount();
        } else {
            toAccount = accountRepository.getById(messageDto.getToAccountId());
        }
        if (fromAccount == null) throw new FJVNException("FromAccount is not valid!");
        if (toAccount == null) throw new FJVNException("ToAccount is not valid!");

        var job = jobRepository.getById(messageDto.getJobId());
        if (job == null) throw new FJVNException("Job is not valid!");

        //check whether the current offer
        //is OFFERING or ACCEPTED

        //need to perform this checks on both cases of fromAcc, in case recruiter cancels the offer
        //after chatting
        //begin check for offer status
        Long freelancerId = null;
        Long recruiterId = null;
        boolean fromAccIsRecruiter = false;
        //If fromAccount's freelancer is not null, then fromAccount is freelancer
        //and toAccount is recruiter
        //and vice versa, fromAcc recruiter not null, fromAcc is recruiter and toAcc is freelancer
        if (toAccount.getFreelancer() != null) {
            fromAccIsRecruiter = true;
            freelancerId = toAccount.getFreelancer().getFreelancerId();
            recruiterId = fromAccount.getRecruiter().getRecruiterId();
        } else if (fromAccount.getFreelancer() != null) {
            fromAccIsRecruiter = false;
            freelancerId = fromAccount.getFreelancer().getFreelancerId();
            recruiterId = toAccount.getRecruiter().getRecruiterId();
        }
        var currentOffer = offerRepository.getOfferByFreelancer_FreelancerIdAndJob_JobId(freelancerId, job.getJobId());
        if (currentOffer == null) throw new FJVNException("This freelancer has not offered to work on this job yet.");
        if (currentOffer.getStatus() != OfferStatus.OFFERING && currentOffer.getStatus() != OfferStatus.ACCEPTED) {
            throw new FJVNException(fromAccIsRecruiter ? "Cannot chat with current Freelancer. Reason: offer is not OFFERING or ACCEPTED." : "Cannot chat with current Recruiter. Reason: offer is not OFFERING or ACCEPTED.");
        }
        //end check for offer status

        //if the sender (fromAccount) is freelancer, then check whether the recruiter has initialized the chat yet
        //How to check? Just check the first message, whether that message has fromAcc is recruiter (valid) or not (invalid).
        if (fromAccount.getFreelancer() != null) {
            Message message = messageRepository.getFirstByFromAccountIdOrToAccountIdOrderBySentTimeAsc(fromAccount.getId(), fromAccount.getId());
            if (message == null || message.getFromAccount().getRecruiter() == null)
                throw new FJVNException("Cannot chat yet, please let recruiter initialize the conversation first.");
        }

        Message message = new Message();
        message.setFromAccount(fromAccount);
        message.setToAccount(toAccount);
        message.setContent(messageDto.getContent());
        message.setSentTime(LocalDateTime.now());
        if (messageDto.getAttachFileUrl() != null && !messageDto.getAttachFileUrl().isEmpty()) {
            message.setAttachFileUrl(messageDto.getAttachFileUrl());
        }
        message.setStatus(MessageStatus.SENT);
        message.setMessageType(MessageType.TEXT);
        message.setJob(job);

        messageRepository.save(message);
        var msgResp = messageMapper.convertToMessageDto(message);

        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule(new JavaTimeModule());
        objMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        var topic = "/msg/" + toAccount.getId() + "/" + messageDto.getJobId();
        mqttService.publish(objMapper.writeValueAsString(msgResp), topic,
                MqttQos.AT_LEAST_ONCE, false);
        return msgResp;
    }
}
