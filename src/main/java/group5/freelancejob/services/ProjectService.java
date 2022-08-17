package group5.freelancejob.services;

import group5.freelancejob.daos.*;
import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.mapper.ProjectMapper;
import group5.freelancejob.models.*;
import group5.freelancejob.daos.Freelancer;
import group5.freelancejob.daos.Portfolio;
import group5.freelancejob.daos.Project;
import group5.freelancejob.daos.SkillProject;
import group5.freelancejob.output.ResponseFreelancerObject;
import group5.freelancejob.repositories.FreelancerRepository;
import group5.freelancejob.repositories.ProjectRepository;
import group5.freelancejob.repositories.SkillProjectRepository;
import group5.freelancejob.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.*;

@Service
@Transactional
public class ProjectService {
    @Autowired
    FreelancerService freelancerService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private FreelancerRepository freelancerRepository;
    @Autowired
    PortfolioService portfolioService;
    @Autowired
    SkillProjectService skillProjectService;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private SkillProjectRepository skillProjectRepository;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    SkillService skillService;

    public PageDto<ResponseProjectDto> getProjects(Integer pageNo, Integer pageSize, List<Long> skillIds, String name) {
        if (!StringUtils.hasLength(name) || !StringUtils.hasText(name)) {
            name = "";
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        //get skills project to filter by skill
        if (!CollectionUtils.isEmpty(skillIds)) {
            List<Skill> skills = skillRepository.findAllById(skillIds);
            List<SkillProject> skillProjects = skillProjectRepository.findAllBySkillIn(skills);
            Page<Project> projectPage = projectRepository.findAllByNameLikeIgnoreCaseAndSkillProjectsIn("%" + name + "%", skillProjects, pageable);
            PageDto<ResponseProjectDto> projectDtoPageDto = new PageDto<>(projectPage, projectMapper.convertToListResponseProjectDto(projectPage.getContent()), pageNo, pageSize);
            return projectDtoPageDto;
        } else {
            Page<Project> projectPage = projectRepository.findAllByNameLikeIgnoreCase("%" + name + "%", pageable);
            PageDto<ResponseProjectDto> projectDtoPageDto = new PageDto<>(projectPage, projectMapper.convertToListResponseProjectDto(projectPage.getContent()), pageNo, pageSize);
            return projectDtoPageDto;
        }
    }

    public PageDto<ResponseProjectDto> getProjectsByFreelancerId(Integer pageNo, Integer pageSize, Long freelancerId, String name) throws FJVNException {
        if (!StringUtils.hasLength(name) || !StringUtils.hasText(name)) {
            name = "";
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        //get skills project to filter by skill
        Freelancer freelancer = freelancerRepository.getById(freelancerId);
        if (freelancer == null) throw new FJVNException("Freelancer with id " + freelancerId + " not found.");
        if (freelancer.getPortfolio() == null) {
            var result = new PageDto<ResponseProjectDto>();
            result.setData(new ArrayList<>());
            result.setHasNextPage(false);
            result.setHasPreviousPage(false);
            result.setPageNo(pageNo);
            result.setPageSize(pageSize);
            result.setTotalCount(0L);
            result.setTotalPage(0);
            return result;
        }
        Page<Project> projectPage = projectRepository.findAllByNameLikeIgnoreCaseAndPortfolioPortfolioId("%" + name + "%", freelancer.getPortfolio().getPortfolioId() , pageable);
        PageDto<ResponseProjectDto> projectDtoPageDto = new PageDto<>(projectPage, projectMapper.convertToListResponseProjectDto(projectPage.getContent()), pageNo, pageSize);
        return projectDtoPageDto;

    }

    public Map<String, Object> getProjectFromProjectId(Long projectId) throws NumberFormatException, Exception {

        Project projectEntity = projectRepository.getById(projectId);
        Portfolio portfolioEntity = portfolioService.getPortfolioByProjectId(projectEntity.getPortfolio().getPortfolioId());

        Freelancer freelancerEntity = freelancerService.getFreelancerInfo(portfolioEntity.getFreelancer().getFreelancerId());

        List<SkillProject> skillProjectEntities = skillProjectRepository.findAllByProjectId(projectEntity.getPrjId());
        List<SkillDto> skillList = skillService.getSkillsBySkillIdList(skillProjectEntities);

        Map<String, Object> mapObj = new LinkedHashMap<>();
        mapObj.put("id", projectEntity.getPrjId());

        ResponseFreelancerObject responseFreelancer = new ResponseFreelancerObject();
        responseFreelancer.setFreelancerId(freelancerEntity.getFreelancerId());
        responseFreelancer.setFreelancerName(freelancerEntity.getFullname());
        responseFreelancer.setImageUrl(freelancerEntity.getAccount().getAvatar());
        responseFreelancer.setShortDescription(freelancerEntity.getShortDescription());
        mapObj.put("freelancer", responseFreelancer);

        mapObj.put("description", projectEntity.getDescription());
        mapObj.put("imageUrl", projectEntity.getImageUrl());
        mapObj.put("name", projectEntity.getName());
        mapObj.put("skills", skillList);

        return mapObj;
    }

    public ResponseProjectDto updateProject(Long projectId, ReqUpdateProjectDto reqUpdateProjectDto) throws FJVNException {
        Project project = projectRepository.getById(projectId);
        if (project == null) throw new FJVNException("Project with id " + projectId + " not found.");
        projectMapper.updateProjectWithReqUpdateProjectDto(reqUpdateProjectDto, project);
        //TODO: make the business clear at skills
        List<Skill> skills = skillRepository.findAllById(reqUpdateProjectDto.getSkills());
        List<SkillProject> newSkillProject = new ArrayList<>();
        if (!(skills == null) && !CollectionUtils.isEmpty(skills)) {
            for (var skill : skills) {
                SkillProject skillProject = new SkillProject(skill, project);
                newSkillProject.add(skillProject);
            }
            //delete old project skill
            System.out.println(project.getSkillProjects());
            long deletedCol = skillProjectRepository.deleteAllByProject(project);
            skillProjectRepository.saveAll(newSkillProject);
        }
        projectRepository.save(project);
        return projectMapper.convertToResponseProjectDto(project);
    }

    public void deleteProject(Long projectId) throws FJVNException {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (!optionalProject.isPresent()) throw new FJVNException("Project with id " + projectId + " not found.");
        //delete skill_project first before deleting project itself
        List<SkillProject> skillProjects = skillProjectRepository.findAllByProjectId(projectId);
        if (!CollectionUtils.isEmpty(skillProjects)) skillProjectRepository.deleteAll(skillProjects);
        projectRepository.deleteById(projectId);
    }


    @Transactional(rollbackFor = {SQLException.class, Exception.class, Throwable.class})
    public Long createNewProject(RequestCreateNewProject reqCreateProject) throws Exception {

        Freelancer freelancer = freelancerRepository.getById(reqCreateProject.getFreelancerId());
        Portfolio portfolio = freelancer.getPortfolio();
        if (portfolio == null) throw new FJVNException("Freelancer does not have portfolio.");

        Project projectEntity = new Project();
        projectEntity.setName(reqCreateProject.getName());
        projectEntity.setImageUrl(reqCreateProject.getImageUrl());
        projectEntity.setDescription(reqCreateProject.getDescription());
        projectEntity.setPortfolio(portfolio);
        projectEntity = projectRepository.save(projectEntity);

        if (projectEntity.getPrjId() != null) {
            skillProjectService.insertSkillProject(reqCreateProject.getSkillIds(), projectEntity);
        }
        return projectEntity.getPrjId();
    }
}