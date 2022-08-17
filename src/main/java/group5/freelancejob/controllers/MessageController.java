package group5.freelancejob.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.models.message.GetMessageRespDto;
import group5.freelancejob.models.message.MessageDto;
import group5.freelancejob.models.CreateMessageRequest;
import group5.freelancejob.models.message.MessageDto2;
import group5.freelancejob.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/message")
public class MessageController {
    @Autowired
    private MessageService _messageService;

    @GetMapping("{accountId}")
    public List<MessageDto> getAllMessageByAccountId(@PathVariable Long accountId) throws FJVNException {
        return _messageService.getAllMessageByAccountId(accountId);
    }

    @GetMapping("{accountId}/{jobId}")
    public Set<GetMessageRespDto> getAllMessageByAccountIdAndJobId(@PathVariable Long accountId, @PathVariable Long jobId) throws FJVNException {
        return _messageService.getAllMessageByAccountIdAndJobId(accountId, jobId);
    }

    @GetMapping("/job/{jobId}/account/{originalAccountId}/with/{targetAccountId}")
    public List<MessageDto2> getAllMessageByJobId(@PathVariable(name="jobId") Long jobId, @PathVariable(name = "originalAccountId") Long originalAccountId,
                                                  @PathVariable(name = "targetAccountId") Long targetAccountId) throws FJVNException {
        return _messageService.getMessagesWithOneAccountInJob(originalAccountId, targetAccountId, jobId);
    }


    @PostMapping()
    // Only recruiter can create chat
    public ResponseEntity<?> createMessage(@RequestBody CreateMessageRequest messageDto) throws JsonProcessingException {
        try {
            return ResponseEntity.ok(_messageService.createMessage(messageDto));
        } catch (FJVNException e) {
            e.printStackTrace();
            return ResponseEntity.status(403).body(Collections.singletonMap("err_msg", e.getMessage()));
        }
    }

}