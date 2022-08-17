package group5.freelancejob.models.message;

import group5.freelancejob.utils.MessageStatus;
import group5.freelancejob.utils.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto2 implements Serializable {
    private String messageId;
    private AccountForMessageDto fromAccount;
    private String content;
    private String attachFileUrl;
    private LocalDateTime sentTime;
    private MessageStatus status;
    private MessageType messageType;
}
