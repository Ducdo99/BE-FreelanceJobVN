package group5.freelancejob.daos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import group5.freelancejob.utils.PaymentHistoryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="PaymentHistory")
public class PaymentHistory implements Serializable {
    @Id
    @Column(name = "transactionCode")
    private String transactionCode;

    @Column(name = "transactionName")
    private String transactionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentHistoryStatus status;

    @Column(name = "transactionDate")
    private LocalDateTime transactionDate;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "accountId")
    private Account account;
}
