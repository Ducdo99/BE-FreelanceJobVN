package group5.freelancejob.models;

import group5.freelancejob.utils.JobStatus;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RespJobDto {
    private Long id;
    private String title;
    private String recruiterName;
    private Long recruiterId;
    private float price;
    private String description;
    private Date duration;
    private List<SkillDto> skills;
    private List<OfferDto> offers;
    private Map<String, Object> offerInfo;
    private GenreDto genre;
    private int jobStatus;
    private JobStatus jobStatusEnum;
    private int noOfOffer;
    private int freelancerRating;
    private int recruiterRating;
}
