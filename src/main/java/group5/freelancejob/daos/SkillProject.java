package group5.freelancejob.daos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Skill_Project")
public class SkillProject implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "skill_id")
    private Skill skill;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "project_id")
    private Project project;
    public SkillProject(Skill skill, Project project) {
        this.skill = skill;
        this.project = project;
    }
}
