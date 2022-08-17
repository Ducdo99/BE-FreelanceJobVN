package group5.freelancejob.mapper;

import group5.freelancejob.daos.Offer;
import group5.freelancejob.models.OfferDto;
import group5.freelancejob.models.OfferWithJobDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class OfferMapper {
    public abstract OfferDto convertToOfferDto(Offer offer);

    @Mapping(target = "jobTitle", source = "job.title")
    @Mapping(target = "jobId", source = "job.jobId")
    @Mapping(target = "jobStatus", source = "job.jobStt")
    @Mapping(target = "freelancerRating", source = "job.freelancerRating")
    @Mapping(target = "recruiterRating", source = "job.recruiterRating")
    public abstract OfferWithJobDto convertToOfferWithJobDto(Offer offer);
}
