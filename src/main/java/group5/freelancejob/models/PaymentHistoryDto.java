package group5.freelancejob.models;

import group5.freelancejob.daos.Account;
import group5.freelancejob.utils.PaymentHistoryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryDto {

    private String transactionCode;

    private String transactionName;

    private PaymentHistoryStatus status;

    private LocalDateTime transactionDate;

    private Double amount;

    private String description;

    private AccountDto account;
}
