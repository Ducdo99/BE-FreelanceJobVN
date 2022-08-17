package group5.freelancejob.controllers;

import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.models.ReqUpdateProjectDto;
import group5.freelancejob.models.RequestCreateNewProject;
import group5.freelancejob.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@RestController
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    /**
     * @apiNote 7.01-get-all-projects
     * @param pageNo
     * @param pageSize
     * @param skillIds
     * @param name
     * @return
     */
    @GetMapping(path = "/projects")
    public ResponseEntity<?> getProjects(
            @RequestParam(name = "pageNo") Integer pageNo,
            @RequestParam(name = "pageSize") Integer pageSize,
            @RequestParam(name = "skill", required = false) List<Long> skillIds,
            @RequestParam(name = "name", required = false) String name
    ) {
        return ResponseEntity.ok().body(projectService.getProjects(pageNo, pageSize, skillIds, name));
    }

    /**
     * @param id
     * @return
     * @throws Exception
     * @Author DucDM
     * @apiNote 7.02-get-project-from-portfolio
     */
    @GetMapping(value = "/project/{project-id}")
    public ResponseEntity<?> getProjectFromProjectId(@PathVariable(value = "project-id") Long id) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(projectService.getProjectFromProjectId(id));
        } catch (NumberFormatException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    /**
     * @apiNote get-project-by-freelancer-id
     */
    @GetMapping(value = "/freelancer/{freelancer-id}/project")
    public ResponseEntity<?> getProjectsByFreelancerId(
            @RequestParam(name = "pageNo") Integer pageNo,
            @RequestParam(name = "pageSize") Integer pageSize,
            @RequestParam(name = "name", required = false) String name,
            @PathVariable(value = "freelancer-id") Long freelancerId) throws FJVNException {
        return ResponseEntity.ok(projectService.getProjectsByFreelancerId(pageNo, pageSize, freelancerId, name));
    }

    /**
     * @apiNote 7.03-create-project
     */
    @PostMapping(value = "/project")
    public ResponseEntity<?> createNewProject(
                                              @RequestBody RequestCreateNewProject reqCreateProject) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createNewProject(reqCreateProject));
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("err_msg",ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("err_msg",ex.getMessage()));
        }
    }

    /**
     * @apiNote 7.04-update-project
     * @param projectId
     * @param reqUpdateProjectDto
     * @return
     * @throws FJVNException
     */
    @PutMapping(path = "/project/{project-id}")
    public ResponseEntity<?> updateProject
            (
             @PathVariable(name = "project-id") Long projectId,
             @RequestBody ReqUpdateProjectDto reqUpdateProjectDto)
            throws FJVNException {
        return ResponseEntity.ok(projectService.updateProject( projectId, reqUpdateProjectDto));
    }

    /**
     * @apiNote 7.05-delete-project
     */
    @DeleteMapping(path = "/project/{project-id}")
    public ResponseEntity<?> deleteProject(
            @PathVariable(name = "project-id") Long projectId
    ) throws FJVNException {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

}
