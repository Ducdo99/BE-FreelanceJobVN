package group5.freelancejob.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewJobReqDto {
    String title;
    String description;
    Date duration;
    Float price;
    Long recruiterId;
    List<Long> skills;
    @NotNull(message = "Loại công việc không được để trống")
    Long genreId;
}
