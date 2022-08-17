package group5.freelancejob.daos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import group5.freelancejob.models.ProjectDto;
import group5.freelancejob.output.ResProjectModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Project")
public class Project implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long prjId;
    @Column(name = "description")
    private String description;
    @Column(name = "name")
    private String name;
    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "skill")
    private List<SkillProject> skillProjects;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    public ProjectDto convertEntityToDTO() {
        ProjectDto dto = new ProjectDto();
        dto.setPrjId(this.prjId);
        dto.setDescription(this.description);
        dto.setName(this.name);
        dto.setImageUrl(this.imageUrl);
        return dto;
    }

    public ResProjectModel convertToResProjectModel() {
        ResProjectModel dto = new ResProjectModel();
        dto.setId(prjId);
        dto.setImageUrl(imageUrl);
        dto.setDescription(description);
        return dto;
    }

    public Project convertDTOToEntity(ProjectDto dto) {
        Project entity = new Project(dto.getPrjId(), dto.getDescription(), dto.getName(), dto.getImageUrl(), null, null);
        return entity;
    }

    public ProjectDto covnertEntityToDTO(Project entity) {
        ProjectDto dto = new ProjectDto(entity.getPrjId(), entity.getDescription(), entity.getName(), entity.getImageUrl());
        return dto;
    }
}