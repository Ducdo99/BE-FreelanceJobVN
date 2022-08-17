package group5.freelancejob.mapper;

import group5.freelancejob.daos.Recruiter;
import group5.freelancejob.models.RecruiterDto;
import group5.freelancejob.models.UpdateProfileRecruiterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class RecruiterMapper {
    @Mappings({
            @Mapping(target="avatar", source = "recruiter.account.avatar"),
            @Mapping(target="phone", source = "recruiter.account.phone"),
            @Mapping(target="email", source = "recruiter.account.email"),
    })
    public abstract RecruiterDto convertToRecruiterDto(Recruiter recruiter);
    public abstract List<RecruiterDto> convertToRecruiterDtoList(List<Recruiter> recruiters);

    @Mappings({
            @Mapping(target="phone", source = "recruiter.account.phone"),
            @Mapping(target="email", source = "recruiter.account.email"),
            @Mapping(target="avatar", source = "recruiter.account.avatar"),
    })
    public abstract UpdateProfileRecruiterDto convertToUpdateProfileRecruiterDto(Recruiter recruiter);
}
