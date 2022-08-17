package group5.freelancejob.models;

import group5.freelancejob.utils.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {
    private Long fromAccountId;
    private Long toAccountId;
    private Long toFreelanceId;
    private Long jobId;
    private String content;
    private String attachFileUrl;
    private LocalDateTime sentTime;
}