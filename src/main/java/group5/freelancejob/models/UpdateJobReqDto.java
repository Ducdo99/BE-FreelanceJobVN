package group5.freelancejob.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJobReqDto {
    String title;
    String description;
    Date duration;
    Float price;
    Long jobId;
    List<Long> skills;
    Long genreId;
}
