package group5.freelancejob.mapper;

import group5.freelancejob.daos.PaymentHistory;
import group5.freelancejob.models.PaymentHistoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PaymentHistoryMapper {
    @Mapping(target = "account", ignore = true)
    public abstract PaymentHistoryDto convertToPaymentHistoryDto(PaymentHistory paymentHistory);
}
