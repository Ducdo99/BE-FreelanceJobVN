package group5.freelancejob.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqUpdateProjectDto {
    private String description;
    private String name;
    private String imageUrl;
    private List<Long> skills;
}
