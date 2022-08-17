package group5.freelancejob.models;

import group5.freelancejob.utils.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JobDto {
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