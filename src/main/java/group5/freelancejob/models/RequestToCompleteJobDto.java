package group5.freelancejob.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestToCompleteJobDto {
    private Long requesterAccountId;
    private Long theOtherChatterAccountId;
    private Long theOtherChatterRecruiterId;
}
