package group5.freelancejob.mybatis.mapper;

import group5.freelancejob.daos.Freelancer;
import group5.freelancejob.models.FreelancerAccSkillDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//@Repository
@Mapper
public interface FreelancerMyBatisMapper {
    List<FreelancerAccSkillDto> selectFreelancerByIdAndListSkills(@Param("skillIds") List<Long> id, @Param("fullname") String name,
                                                                  @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    Integer countSelectFreelancerByIdAndListSkills(@Param("skillIds") List<Long> id, @Param("fullname") String name);
}
