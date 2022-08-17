package group5.freelancejob.models.offerbyjobid;

import group5.freelancejob.models.SkillDto;
import group5.freelancejob.utils.JobStatus;
import group5.freelancejob.utils.OfferStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferByJobIdDto {
    private Long jobId;
    private String title;
    private JobStatus status;
    private List<OfferByJobDto> offers;
    private Map<String, Object> offerInfo;
}