package group5.freelancejob.services;

import group5.freelancejob.daos.Account;
import group5.freelancejob.daos.Freelancer;
import group5.freelancejob.daos.Portfolio;
import group5.freelancejob.daos.Recruiter;
import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.mapper.FreelancerMapper;
import group5.freelancejob.mapper.PortfolioMapper;
import group5.freelancejob.mapper.RecruiterMapper;
import group5.freelancejob.models.FreelancerDto;
import group5.freelancejob.models.PortfolioDto;
import group5.freelancejob.models.ReqUpdatePortfolioDto;
import group5.freelancejob.models.UpdateProfileRecruiterDto;
import group5.freelancejob.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
public class PortfolioService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    FreelancerRepository freelancerRepository;
    @Autowired
    RecruiterRepository recruiterRepository;
    @Autowired
    PortfolioRepository portfolioRepository;
    @Autowired
    SkillRepository skillRepository;
    @Autowired
    private PortfolioMapper portfolioMapper;
    @Autowired
    private FreelancerMapper freelancerMapper;
    @Autowired
    private RecruiterMapper recruiterMapper;


    public ResponseEntity<?> getPortfolio(Long freelancerId) throws Exception {
        FreelancerDto freelancer = freelancerRepository.getById(freelancerId).convertToFreelancerDto();

        HashMap<String, Object> respObj = new LinkedHashMap<>();
        respObj.put("name", freelancer.getFullname());
        respObj.put("roleAtWork", freelancer.getRoleAtWork());
        respObj.put("shortDescription", freelancer.getShortDescription());
        respObj.put("description", freelancer.getDescription());
        respObj.put("avatar", freelancerRepository.getById(freelancerId).getAccount().getAvatar());
        respObj.put("skills", skillRepository.findByFreelancerId(freelancerId).stream().map(skill -> skill.convertToSkillDto()).collect(Collectors.toList()));

        return ResponseEntity.status(HttpStatus.OK).body(respObj);
    }

    public Portfolio getPortfolioByProjectId(Long id) throws NumberFormatException, Exception {
        return portfolioRepository.getById(id);
    }

    public PortfolioDto updatePortfolio(Long freelancerId, ReqUpdatePortfolioDto portfolioUpdateInfo) throws FJVNException {

        Freelancer freelancer = freelancerRepository.getById(freelancerId);
        if (freelancer == null) throw new FJVNException("Freelancer with id " + freelancerId + " is not valid.");
        Portfolio portfolio = freelancer.getPortfolio();
        //if portfolio is null, auto create new
        //shouldn't do this when working at company
        if (portfolio == null) {
            portfolio = new Portfolio();
            portfolio.setDescription(portfolioUpdateInfo.getPortfolioDescription());
            portfolio.setFreelancer(freelancer);
        } else {
            portfolioMapper.updatePortfolioWithReqUpdatePortfolioDto(portfolioUpdateInfo, portfolio);
        }
        //update freelancer, because this API acts as general profile update
        freelancerMapper.updateFreelancerWithReqUpdatePortfolioDto(portfolioUpdateInfo, freelancer);

        //update avatar
        Account account = freelancer.getAccount();
        if (portfolioUpdateInfo.getAvatar() != null) {
            account.setAvatar(portfolioUpdateInfo.getAvatar());
        }
        if (portfolioUpdateInfo.getPhone() != null) {
            account.setPhone(portfolioUpdateInfo.getPhone());
        }
        freelancerRepository.save(freelancer);
        portfolioRepository.save(portfolio);
        accountRepository.save(account);
        return portfolioMapper.convertToPortfolioDto(portfolio);
    }

    public UpdateProfileRecruiterDto updateRecruiter(Long recruiterId, ReqUpdatePortfolioDto body) {
        Recruiter recruiter = recruiterRepository.getById(recruiterId);
        recruiter.setFullname(body.getFullName());
        recruiter.setDescription(body.getDescription());
        recruiter.setShortDescription(body.getShortDescription());

        recruiterRepository.save(recruiter);

        Account account = recruiter.getAccount();
        if (body.getPhone() != null) {
            account.setPhone(body.getPhone());
        }
        if (body.getAvatar() != null) {
            account.setAvatar(body.getAvatar());
        }
        accountRepository.save(account);

        return recruiterMapper.convertToUpdateProfileRecruiterDto(recruiter);
    }
}
