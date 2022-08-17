package group5.freelancejob.mapper;

import group5.freelancejob.daos.Freelancer;
import group5.freelancejob.daos.Job;
import group5.freelancejob.daos.Offer;
import group5.freelancejob.daos.Skill;
import group5.freelancejob.models.JobDto;
import group5.freelancejob.models.RespJobDto;
import group5.freelancejob.models.SkillDto;
import group5.freelancejob.models.offerbyjobid.FreelancerOfferByJobDto;
import group5.freelancejob.models.offerbyjobid.OfferByJobDto;
import group5.freelancejob.models.offerbyjobid.OfferByJobIdDto;
import group5.freelancejob.utils.JobStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class JobMapper {
    public abstract JobDto convertToJobDto(Job job);
    @Mappings({
            @Mapping(target = "offers", source = "job.offers"),
            @Mapping(target = "status", source = "job.jobStt")
    })
    public abstract OfferByJobIdDto convertToOfferByJobIdDto(Job job);

    public abstract OfferByJobDto convertToOfferByJobDto(Offer offer);

    @Mappings({
            @Mapping(target = "phone", source = "account.phone"),
            @Mapping(target = "email", source = "account.email"),
            @Mapping(target = "avatar", source = "account.avatar")
    })
    public abstract FreelancerOfferByJobDto convertToFreelancerOfferByJobDto(Freelancer freelancer);

    public abstract SkillDto convertToSkillDto(Skill skill);

    @Mappings({
            @Mapping(target = "id", source = "jobId"),
            @Mapping(target = "jobStatus", source = "jobStt", qualifiedByName = {"mapJobEnumToJobIntAtResponse"})
    })
    public abstract RespJobDto convertToRespJobDto(Job job);

    @Named("mapJobEnumToJobIntAtResponse")
    public int mapJobEnumtoJobIntAtResponse(JobStatus jobStatus) {
        if (jobStatus != null)
            return jobStatus.ordinal();
        else return -1;
    }
}
