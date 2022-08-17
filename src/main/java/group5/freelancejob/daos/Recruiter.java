package group5.freelancejob.daos;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Recruiter")
public class Recruiter implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long recruiterId;
    @Column(name = "fullname")
    private String fullname;
    @Column(name = "rating")
    private String rating;
    @Column(name = "short_description")
    private String shortDescription;
    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "acc_id", referencedColumnName = "id")
    private Account account;

    @OneToMany(mappedBy = "recruiter")
    private List<Job> jobs;
}
