package group5.freelancejob.daos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import group5.freelancejob.models.RespJobDto;
import group5.freelancejob.utils.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Job")
public class Job implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long jobId;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "duration")
    private Date duration;
    @Column(name = "price")
    private float price;
    @Enumerated(EnumType.STRING)
    @Column(name = "job_stt")
    private JobStatus jobStt;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "modified_date")
    private Date modifiedDate;
    @Column(name = "freelancer_rating")
    private int freelancerRating;
    @Column(name = "recruiter_rating")
    private int recruiterRating;

    @OneToMany(mappedBy = "job")
    private List<JobSkill> jobSkills;
    @OneToMany(mappedBy = "job")
    private List<Offer> offers;
    @OneToMany(mappedBy = "job")
    private List<Message> messages;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "recruiter_id")
    private Recruiter recruiter;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "genre_id")
    private Genre genre;

    public RespJobDto convertToRespJobDto() {
        RespJobDto dto = new RespJobDto();
        dto.setId(jobId);
        dto.setTitle(title);
        dto.setPrice(price);
        dto.setDescription(description);
        dto.setDuration(duration);
        return dto;
    }
}
