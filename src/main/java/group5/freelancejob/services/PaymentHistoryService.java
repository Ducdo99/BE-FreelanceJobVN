package group5.freelancejob.services;

import group5.freelancejob.daos.PaymentHistory;
import group5.freelancejob.mapper.PaymentHistoryMapper;
import group5.freelancejob.models.PaymentHistoryDto;
import group5.freelancejob.repositories.AccountRepository;
import group5.freelancejob.repositories.PaymentHistoryRepository;
import group5.freelancejob.utils.Constant;
import group5.freelancejob.utils.PaymentHistoryStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
public class PaymentHistoryService {
    private Logger logger = Logger.getLogger(PaymentHistoryService.class.getName());
    private final PaymentHistoryRepository _paymentHistoryRepository;
    private final AccountRepository _accountRepository;

    private final PaymentHistoryMapper _paymentHistoryMapper;

    public PaymentHistoryService(PaymentHistoryRepository paymentHistoryRepository, AccountRepository accountRepository, PaymentHistoryMapper paymentHistoryMapper) {
        _paymentHistoryRepository = paymentHistoryRepository;
        _accountRepository = accountRepository;
        _paymentHistoryMapper = paymentHistoryMapper;
    }

    public List<PaymentHistoryDto> getPaymentHistoryById(Long id) {
        var account = _accountRepository.getById(id);
        var listOfPaymentHistory = account.getPaymentHistories();
        listOfPaymentHistory.sort((a, b) -> b.getTransactionDate().compareTo(a.getTransactionDate()));
        return listOfPaymentHistory.stream().map(_paymentHistoryMapper::convertToPaymentHistoryDto)
                .collect(java.util.stream.Collectors.toList());
    }

    public void createOrderHistory(Long accId, String amount, String transactionCode,
                                   String description) {
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setAmount(Double.valueOf(amount));
        paymentHistory.setTransactionCode(transactionCode);
        paymentHistory.setDescription(description);
        paymentHistory.setTransactionName(Constant.PAYMENT_TYPE_TOPUP);
        paymentHistory.setStatus(PaymentHistoryStatus.PENDING);
        paymentHistory.setTransactionDate(LocalDateTime.now());

        var account = _accountRepository.getById(Long.valueOf(accId));
        paymentHistory.setAccount(account);


        _paymentHistoryRepository.save(paymentHistory);
    }

    @Transactional
    // Must have @Transactional for LazyLoad Hibernate to work
    // without LazyInitilizationException
    public void updateOrderHistory(String transactionCode, Long accId, PaymentHistoryStatus status) {
        var paymentHistory = _paymentHistoryRepository.getById(transactionCode);
        var account = _accountRepository.getById(accId);
        if (status.equals(PaymentHistoryStatus.SUCCESS))
            account.setDeposit(Math.round(account.getDeposit() + paymentHistory.getAmount()));
        paymentHistory.setStatus(status);
        _paymentHistoryRepository.save(paymentHistory);
    }
}
