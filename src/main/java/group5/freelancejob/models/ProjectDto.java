package group5.freelancejob.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectDto implements Serializable {
    private Long prjId;
    private String description;
    private String name;
    private String imageUrl;
}
