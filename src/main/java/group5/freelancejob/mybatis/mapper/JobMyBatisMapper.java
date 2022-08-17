package group5.freelancejob.mybatis.mapper;

import group5.freelancejob.daos.Job;
import group5.freelancejob.models.RespJobDto;
import group5.freelancejob.utils.JobStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface JobMyBatisMapper {
    @SelectProvider(type = JobMyBatisSql.class, method = "getJobByName")
    public List<Job> getJobByName(String name);

    List<RespJobDto> selectJobFromTitleAndSkill(@Param("title") String title,
                                                @Param("skillIds") List<Long> skillIds,
                                                @Param("status") JobStatus jobStatus,
                                                @Param("genreId") Long genreId,
                                                @Param("offset") Integer pageNo,
                                                @Param("pageSize") Integer pageSize);

    Integer countSelectJobFromTitleAndSkill(@Param("title") String title,
                                            @Param("skillIds") List<Long> skillIds,
                                            @Param("genreId") Long genreId,
                                            @Param("status") JobStatus jobStatus);
}
