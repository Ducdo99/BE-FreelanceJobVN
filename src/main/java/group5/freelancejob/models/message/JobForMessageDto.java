package group5.freelancejob.models.message;

import group5.freelancejob.models.OfferDto;
import group5.freelancejob.utils.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobForMessageDto {
    private Long jobId;
    private String title;
    private String description;
    private Date duration;
    private float price;
    private JobStatus jobStt;
    private Date createdDate;
    private Date modifiedDate;
    private int freelancerRating;
    private int recruiterRating;
}
