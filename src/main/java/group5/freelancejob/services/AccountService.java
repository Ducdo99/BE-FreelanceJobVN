package group5.freelancejob.services;

import group5.freelancejob.daos.*;
import group5.freelancejob.exception.ExistedUserException;
import group5.freelancejob.repositories.*;
import group5.freelancejob.security.FVNUserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLDataException;
import java.util.*;

@Service
public class AccountService {
    private final AccountRepository _accRepository;
    private final FreelancerRepository _freelancerRepository;
    private final RecruiterRepository _recruiterRepository;
    private final RoleRepository _roleRepository;

    private final PortfolioRepository _portfolioRepository;

    public AccountService(AccountRepository accRepository, FreelancerRepository freelancerRepository, RecruiterRepository recruiterRepository, RoleRepository roleRepository, PortfolioRepository portfolioRepository) {
        _accRepository = accRepository;
        _freelancerRepository = freelancerRepository;
        _recruiterRepository = recruiterRepository;
        _roleRepository = roleRepository;
        _portfolioRepository = portfolioRepository;
    }

    public float getBalance(Long id) {
        Account account = _accRepository.getById(id);
        return account.getDeposit();
    }

    public Account getAccountDetailFromEmail(String email) throws UsernameNotFoundException {
        Account foundAcc = _accRepository.findFirstByEmail(email);
        if (foundAcc == null) throw new UsernameNotFoundException("User not found in DB!");
        return foundAcc;
    }

    public Account getAccountDetailFromId(Long accountId) throws UsernameNotFoundException {
        Account foundAcc = _accRepository.getById(accountId);
        if (foundAcc == null) throw new UsernameNotFoundException("User not found in DB!");
        return foundAcc;
    }

    @Transactional(rollbackFor = {SQLDataException.class, Exception.class, Throwable.class})
    public String createAccount(String email, String avatar, String roleToAdd, String name) throws Exception {
        if (_accRepository.findFirstByEmail(email) != null) {
            throw new ExistedUserException("Tài khoản đã tồn tại!");
        }

        Role usrRole = _roleRepository.findByNameEqualsIgnoreCase(roleToAdd);

        if (usrRole == null) {
            throw new Exception("Vai trò không tồn tại");
        }

        Account newAcc = new Account();
        newAcc.setEmail(email);
        newAcc.setAvatar(avatar);
        newAcc.setRole(usrRole);

        String emailDomain = StringUtils.substringAfter(email, "@");
        boolean isFPT = emailDomain.equalsIgnoreCase("fpt.edu.vn");
        boolean isGmail = emailDomain.equalsIgnoreCase("gmail.com");

        if ("recruiter".equals(roleToAdd)) {
            if (!isGmail) {
                throw new Exception("Chỉ có account @gmail.com mới được đăng ký recruiter");
            }
            _accRepository.save(newAcc);

            Recruiter newRecruiter = new Recruiter();
            newRecruiter.setFullname(name);
            newRecruiter.setAccount(newAcc);

            _recruiterRepository.save(newRecruiter);
        } else {
            if (!isFPT) {
                throw new Exception("Chỉ có account @fpt.edu.vn mới được đăng ký freelancer");
            }
            _accRepository.save(newAcc);

            Freelancer newFreelancer = new Freelancer();
            newFreelancer.setFullname(name);
            newFreelancer.setAccount(newAcc);

            Portfolio newPortfolio = new Portfolio();
            newPortfolio.setFreelancer(newFreelancer);


            _freelancerRepository.save(newFreelancer);
            _portfolioRepository.save(newPortfolio);
        }

        return newAcc.getId().toString();
    }

    public FVNUserPrincipal getUserPrinciple(Account acc) {
        FVNUserPrincipal userPrincipal = new FVNUserPrincipal();
        String roleName = acc.getRole().getName();
        //Actually there's only 1 role per acc now
        Set<String> role = new HashSet<>();
        userPrincipal.setAccountId(acc.getId());
        userPrincipal.setEmail(acc.getEmail());
        userPrincipal.setPhone(acc.getPhone());
        userPrincipal.setRoleName(roleName);
        userPrincipal.setAvatar(acc.getAvatar());

        if ("recruiter".equals(roleName)) {
            userPrincipal.setUserId(acc.getRecruiter().getRecruiterId());
            userPrincipal.setFullName(acc.getRecruiter().getFullname());
            userPrincipal.setDesc(acc.getRecruiter().getDescription());
            userPrincipal.setShortDesc(acc.getRecruiter().getShortDescription());
        }
        if ("freelancer".equals(roleName)) {
            userPrincipal.setUserId(acc.getFreelancer().getFreelancerId());
            userPrincipal.setFullName(acc.getFreelancer().getFullname());
            userPrincipal.setDesc(acc.getFreelancer().getDescription());
            userPrincipal.setShortDesc(acc.getFreelancer().getShortDescription());
        }

        role.add(roleName);
        userPrincipal.setAuthorities(role);

        return userPrincipal;
    }

    public List<Map<String, String>> getAllAccountAndInfo() {
        var list = _accRepository.getAll();
        List<Map<String, String>> listOfMapResp = new ArrayList<>();
        for (Account acc : list) {
            if (acc.getRole().getName().equals("admin")) continue;
            Map<String, String> mapOfInfo = new HashMap<>();
            String fullName;
            mapOfInfo.put("id", acc.getId().toString());
            mapOfInfo.put("avatar", acc.getAvatar());
            mapOfInfo.put("email", acc.getEmail());
            mapOfInfo.put("phone", acc.getPhone());
            mapOfInfo.put("role", acc.getRole().getName());
            mapOfInfo.put("deposit", String.valueOf(acc.getDeposit()));
            if (acc.getFreelancer() != null) {
                fullName = acc.getFreelancer().getFullname();
            } else {
                fullName = acc.getRecruiter().getFullname();
            }
            mapOfInfo.put("fullName", fullName);

            listOfMapResp.add(mapOfInfo);
        }
        return listOfMapResp;
    }
}
