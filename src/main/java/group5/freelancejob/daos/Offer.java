package group5.freelancejob.daos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import group5.freelancejob.utils.OfferStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Offer")
public class Offer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long offerId;
    @Column(name = "offer_price")
    private float offerPrice;
    @Column(name = "time_to_complete")
    @Nationalized
    private String timeToComplete;
    @Column(name = "experience")
    @Nationalized
    private String experience;
    @Column(name = "planning")
    @Nationalized
    private String planning;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OfferStatus status;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "job_id")
    private Job job;
}
