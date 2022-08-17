package group5.freelancejob.mapper;

import group5.freelancejob.daos.Freelancer;
import group5.freelancejob.models.FreelancerDto;
import group5.freelancejob.models.ReqUpdatePortfolioDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class FreelancerMapper {

    public abstract FreelancerDto convertToFreelancerDto(Freelancer freelancer);
    @Mappings({
            @Mapping(target = "description", source = "dto.description"),
            @Mapping(target = "fullname", source = "dto.fullName")
    })
    public abstract void updateFreelancerWithReqUpdatePortfolioDto(ReqUpdatePortfolioDto dto, @MappingTarget Freelancer freelancer);
}
