package group5.freelancejob.daos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import group5.freelancejob.utils.MessageStatus;
import group5.freelancejob.utils.MessageType;
import group5.freelancejob.utils.PaymentHistoryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="Message")
public class Message implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "messageId")
    private UUID messageId;

    @Column(name = "content")
    private String content;

    @Column(name = "attachFileUrl")
    private String attachFileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "messageType")
    private MessageType messageType;

    @Column(name = "sentTime")
    private LocalDateTime sentTime;

    @ManyToOne
    @JoinColumn(name = "jobId")
    private Job job;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "fromAccountId")
    private Account fromAccount;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "toAccountId")
    private Account toAccount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return messageId.equals(message.messageId) && content.equals(message.content) && Objects.equals(attachFileUrl, message.attachFileUrl) && status == message.status && messageType == message.messageType && sentTime.equals(message.sentTime) && job.equals(message.job) && fromAccount.equals(message.fromAccount) && toAccount.equals(message.toAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, content, attachFileUrl, status, messageType, sentTime, job, fromAccount, toAccount);
    }
}