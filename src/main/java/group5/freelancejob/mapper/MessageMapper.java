package group5.freelancejob.mapper;

import group5.freelancejob.daos.Account;
import group5.freelancejob.daos.Message;
import group5.freelancejob.models.message.AccountForMessageDto;
import group5.freelancejob.models.message.GetMessageRespDto;
import group5.freelancejob.models.message.MessageDto;
import group5.freelancejob.models.message.MessageDto2;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class MessageMapper {
    @Mappings({
            @Mapping(target = "fromAccount.accId", source = "fromAccount.id"),
            @Mapping(target = "toAccount.accId", source = "toAccount.id"),
    })
    public abstract MessageDto convertToMessageDto(Message message);

    @Mappings({
            @Mapping(target = "fromAccount.accId", source = "fromAccount.id"),
    })
    public abstract MessageDto2 convertToMessageDto2(Message message);

    //source .: source can be anything
    @Mapping(target = "targetUser", source = ".", qualifiedByName = {"determineTargetAccount"})
    public abstract GetMessageRespDto populateWithTargetAccount(Message message, @Context Account currentAccount);

    @Mapping(target = "accId", source = "account.id")
    public abstract AccountForMessageDto convertToAccountForMessageDto(Account account);

    @Named("determineTargetAccount")
    public AccountForMessageDto determineTargetAccount(Message message, @Context Account currentAccount) {
        if (message.getFromAccount().getId() == currentAccount.getId()) {
            return (convertToAccountForMessageDto(message.getToAccount()));
        } else if (message.getToAccount().getId() == currentAccount.getId()) {
            return (convertToAccountForMessageDto(message.getFromAccount()));
        }
        return null;
    }


}
