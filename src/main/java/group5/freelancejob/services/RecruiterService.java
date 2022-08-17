package group5.freelancejob.services;

import group5.freelancejob.daos.Recruiter;
import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.mapper.GeneralMapper;
import group5.freelancejob.mapper.RecruiterMapper;
import group5.freelancejob.models.PageDto;
import group5.freelancejob.models.RecruiterDto;
import group5.freelancejob.repositories.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecruiterService {
    @Autowired
    private RecruiterRepository recruiterRepository;
    @Autowired
    private RecruiterMapper recruiterMapper;
    @Autowired
    private GeneralMapper generalMapper;

    public RecruiterDto getRecruiterById(Long recruiterId) throws FJVNException {
        Recruiter recruiter = recruiterRepository.getById(recruiterId);
        if (recruiter == null) {
            throw new FJVNException("Recruiter with id " + recruiterId + " not found.");
        }
        return recruiterMapper.convertToRecruiterDto(recruiter);
    }

    public PageDto<RecruiterDto> getRecruiters(Integer pageNo, Integer pageSize, List<Integer> skills, String name) {
        if (!StringUtils.hasLength(name) || !StringUtils.hasText(name)) {
            name = "";
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Recruiter> recruiters = recruiterRepository.findAllByFullnameLike("%" + name + "%", pageable);

        PageDto<RecruiterDto> recruiterDtoPageDto = new PageDto<>(recruiters, recruiterMapper.convertToRecruiterDtoList(recruiters.getContent()), pageNo, pageSize);
        return recruiterDtoPageDto;
    }
}
