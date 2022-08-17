package group5.freelancejob.models.message;

import group5.freelancejob.models.OfferDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMessageRespDto implements Serializable {
    private AccountForMessageDto targetUser;
    private OfferDto currentOffer;
    private List<MessageDto2> messages;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetMessageRespDto that = (GetMessageRespDto) o;
        if (that.getTargetUser() == null || this.getTargetUser() == null) return false;
        if (that.getTargetUser().getAccId() != this.getTargetUser().getAccId()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetUser.getAccId());
    }
}
