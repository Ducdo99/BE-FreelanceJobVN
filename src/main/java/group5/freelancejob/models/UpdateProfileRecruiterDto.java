package group5.freelancejob.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRecruiterDto {
    private String email;
    private String phone;
    private String fullname;
    private String avatar;
    private String shortDescription;
    private String description;
}
