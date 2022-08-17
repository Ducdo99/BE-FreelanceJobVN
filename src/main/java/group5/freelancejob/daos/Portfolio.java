package group5.freelancejob.daos;

import group5.freelancejob.models.PortfolioDto;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Portfolio")
public class Portfolio implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long portfolioId;
    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "freelancer_id", referencedColumnName = "id")
    private Freelancer freelancer;

    @OneToMany(mappedBy = "portfolio")
    private List<Project> projects;

    public Portfolio covnertDTOToEntity(PortfolioDto dto) {
        Portfolio entity = new Portfolio(dto.getPortfolioId(), dto.getDescription(),null, null);
        return entity;
    }

    public PortfolioDto convertEntityToDTO() {
        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.setPortfolioId(this.portfolioId);
        portfolioDto.setDescription(this.description);
        return portfolioDto;
    }
}
