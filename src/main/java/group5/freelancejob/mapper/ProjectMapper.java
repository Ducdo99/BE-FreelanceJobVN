package group5.freelancejob.mapper;

import group5.freelancejob.daos.Project;
import group5.freelancejob.models.ProjectDto;
import group5.freelancejob.models.ReqUpdatePortfolioDto;
import group5.freelancejob.models.ReqUpdateProjectDto;
import group5.freelancejob.models.ResponseProjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProjectMapper {

    public abstract ResponseProjectDto convertToResponseProjectDto(Project project);
    public abstract List<ResponseProjectDto> convertToListResponseProjectDto(List<Project> projects);
    public abstract void updateProjectWithReqUpdateProjectDto(ReqUpdateProjectDto dto, @MappingTarget Project project);
}
