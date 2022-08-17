package group5.freelancejob.mapper;

import group5.freelancejob.daos.Portfolio;
import group5.freelancejob.daos.Project;
import group5.freelancejob.models.PortfolioDto;
import group5.freelancejob.models.ReqUpdatePortfolioDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class PortfolioMapper {
    @Mappings({
            @Mapping(target = "avatar", source = "portfolio.freelancer.account.avatar"),
            @Mapping(target = "phone", source = "portfolio.freelancer.account.phone"),
    })
    public abstract PortfolioDto convertToPortfolioDto(Portfolio portfolio);
    @Mappings({
            @Mapping(target = "description", source = "dto.portfolioDescription"),
    })
    public abstract void updatePortfolioWithReqUpdatePortfolioDto(ReqUpdatePortfolioDto dto, @MappingTarget Portfolio portfolio);
}
