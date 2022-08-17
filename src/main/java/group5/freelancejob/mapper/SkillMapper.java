package group5.freelancejob.mapper;

import group5.freelancejob.daos.Skill;
import group5.freelancejob.models.SkillDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class SkillMapper {
    public abstract SkillDto convertToSkillDto(Skill skill);
}
