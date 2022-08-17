package group5.freelancejob.controllers;

import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.models.RecruiterDto;
import group5.freelancejob.services.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecruiterController {
    @Autowired
    private RecruiterService recruiterService;

    /**
     * @apiNote 8.02-get-all-recruiter
     */
    @GetMapping(path = "/recruiters")
    public ResponseEntity<?> getRecruiters(@RequestParam(name = "pageNo") Integer pageNo,
                                           @RequestParam(name = "pageSize") Integer pageSize,
                                           @RequestParam(name = "skill", required = false) List<Integer> skills,
                                           @RequestParam(name = "name", required = false) String name) throws FJVNException {
        return ResponseEntity.ok(recruiterService.getRecruiters(pageNo, pageSize, skills, name));
    }
    /**
     * @apiNote 8.01-ger-recruiter-by-id
     * @param recruiterId
     * @return
     * @throws FJVNException
     */
    @GetMapping(path = "/recruiter")
    public ResponseEntity<RecruiterDto> getRecruiterById(@RequestParam("recruiterId") Long recruiterId) throws FJVNException {
        return ResponseEntity.ok(recruiterService.getRecruiterById(recruiterId));
    }

}
