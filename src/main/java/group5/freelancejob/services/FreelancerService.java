package group5.freelancejob.services;

import group5.freelancejob.daos.Account;
import group5.freelancejob.daos.Freelancer;
import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.models.FreelancerAccSkillDto;
import group5.freelancejob.models.SkillDto;
import group5.freelancejob.mybatis.mapper.FreelancerMyBatisMapper;
import group5.freelancejob.output.ResFreelancerAccSkillProjectModel;
import group5.freelancejob.repositories.AccountRepository;
import group5.freelancejob.repositories.FreelancerRepository;
import group5.freelancejob.repositories.ProjectRepository;
import group5.freelancejob.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FreelancerService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    FreelancerRepository freelancerRepository;
    @Autowired
    SkillRepository skillRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    private FreelancerMyBatisMapper freelancerMyBatisMapper;

    public Object getAllFreelancers(int pageNo, int pageSize, List<Long> skillIds, String fullname) throws FJVNException {
        if (pageNo <= 0) throw new FJVNException("Page number cannot lower than 1");
        pageNo -= 1;
        var queryResult = freelancerMyBatisMapper.selectFreelancerByIdAndListSkills(skillIds, fullname, pageNo*pageSize, pageSize);
        for (var freelancer : queryResult) {
            Account account = accountRepository.findByFreelancer_FreelancerId(freelancer.getId());
            freelancer.setPhone(account.getPhone());
            freelancer.setEmail(account.getEmail());
            freelancer.setAvatar(account.getAvatar());

            List<SkillDto> skills = skillRepository.findByFreelancerId(freelancer.getId()).stream().map(skill -> skill.convertToSkillDto()).collect(Collectors.toList());
            freelancer.setSkills(skills);
        }
        Integer totalElements = freelancerMyBatisMapper.countSelectFreelancerByIdAndListSkills(skillIds, fullname);
        HashMap<String, Object> respObj = new LinkedHashMap<>();
        respObj.put("data", queryResult);
        respObj.put("pageNo", pageNo + 1);
        respObj.put("pageSize", pageSize);
        respObj.put("totalCount", totalElements);
        respObj.put("totalPage", Math.ceil(totalElements.floatValue()/Integer.valueOf(pageSize).floatValue()));
        respObj.put("hasPreviousPage", pageNo + 1 > 1);
        respObj.put("hasNextPage", pageNo + 1 < Math.ceil(totalElements.floatValue()/Integer.valueOf(pageSize).floatValue()));
        return respObj;
    }

    private ResponseEntity<?> getResponseEntity(int pageNo, int pageSize, Page<Freelancer> freelancerPage) {
        List<FreelancerAccSkillDto> freelancers = freelancerPage.getContent().stream().map(freelancer -> freelancer.convertToFreelancerAccSkillDto()).collect(Collectors.toList());
        for (int i = 0; i < freelancers.size(); i++) {
            Account account = accountRepository.findByFreelancer_FreelancerId(freelancers.get(i).getId());
            freelancers.get(i).setPhone(account.getPhone());
            freelancers.get(i).setEmail(account.getEmail());
            freelancers.get(i).setAvatar(account.getAvatar());

            List<SkillDto> skills = skillRepository.findByFreelancerId(freelancers.get(i).getId()).stream().map(skill -> skill.convertToSkillDto()).collect(Collectors.toList());
            freelancers.get(i).setSkills(skills);
        }

        HashMap<String, Object> respObj = new LinkedHashMap<>();
        respObj.put("data", freelancers);
        respObj.put("pageNo", pageNo);
        respObj.put("pageSize", pageSize);
        respObj.put("totalPage", freelancerPage.getTotalPages());
        respObj.put("hasPreviousPage", pageNo > 1);
        respObj.put("hasNextPage", pageNo < freelancerPage.getTotalPages());

        return ResponseEntity.status(HttpStatus.OK).body(respObj);
    }

    public Freelancer getFreelancerInfo(Long freelancerId) {
        return freelancerRepository.getById(freelancerId);
    }

    public ResFreelancerAccSkillProjectModel getFreelancer(Long freelancerId) throws Exception {
        Freelancer freelancer = freelancerRepository.findFreelancerAndSkillByFreelancerId(freelancerId);
        if (freelancer == null) throw new Exception("Freelancer with id " + freelancerId + " is not exist.");
        ResFreelancerAccSkillProjectModel resFreelancer = freelancer.convertToResFreelancerAccSkillProjectModel();

        Account account = accountRepository.findByFreelancer_FreelancerId(freelancerId);
        resFreelancer.setPhone(account.getPhone());
        resFreelancer.setEmail(account.getEmail());
        resFreelancer.setAvatar(account.getAvatar());

        resFreelancer.setSkills(skillRepository.findByFreelancerId(freelancerId).stream().map(skill -> skill.convertToSkillDto()).collect(Collectors.toList()));
        resFreelancer.setProjects(projectRepository.findByPortfolio_Freelancer_FreelancerIdOrderByPrjIdAsc(freelancerId).stream().map(project -> project.convertToResProjectModel()).collect(Collectors.toList()));

        return resFreelancer;
    }
}