package group5.freelancejob.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqAddMessageDto {
    private Long fromAccountId;
    private Long toAccountId;
    private String attachmentUrl;
    private String messageContent;
    private LocalDateTime sentTime;
}
