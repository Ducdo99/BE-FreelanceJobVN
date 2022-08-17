package group5.freelancejob.daos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "Job_Skill")
public class JobSkill implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long jobSkillId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "job_id")
    private Job job;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "skill_id")
    private Skill skill;
}