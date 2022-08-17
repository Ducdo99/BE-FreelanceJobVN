package group5.freelancejob.models.message;

import group5.freelancejob.models.AccountDto;
import group5.freelancejob.models.JobDto;
import group5.freelancejob.models.OfferDto;
import group5.freelancejob.utils.MessageStatus;
import group5.freelancejob.utils.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String messageId;
    private AccountForMessageDto fromAccount;
    private AccountForMessageDto toAccount;
    private String content;
    private String attachFileUrl;
    private LocalDateTime sentTime;
    private MessageStatus status;
    private MessageType messageType;
    private JobDto job;
    //current offer get by freelancerId and jobId
    private OfferDto currentOffer;
}
