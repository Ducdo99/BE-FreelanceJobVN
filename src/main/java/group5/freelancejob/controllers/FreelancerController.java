package group5.freelancejob.controllers;

import group5.freelancejob.mybatis.mapper.FreelancerMyBatisMapper;
import group5.freelancejob.services.FreelancerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FreelancerController {
    @Autowired
    private FreelancerService freelancerService;

    /**
     * @param pageNo
     * @param pageSize
     * @param skills
     * @param name
     * @return
     * @throws Exception
     * @apiNote 2.01-get-all-freelancers
     */
    @GetMapping(value = "/freelancers")
    public ResponseEntity<?> getAllFreelancers(@RequestParam(value = "pageNo") int pageNo,
                                               @RequestParam(value = "pageSize") int pageSize,
                                               @RequestParam(value = "skill", required = false) List<Long> skills,
                                               @RequestParam(value = "name", required = false) String name) throws Exception {
        try {
            return ResponseEntity.ok(freelancerService.getAllFreelancers(pageNo, pageSize, skills, name));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * @param freelancerId
     * @return
     * @throws Exception
     * @apiNote 2.02-get-freelancer
     */
    @GetMapping(value = "/freelancer/{id}")
    public ResponseEntity<?> getFreelancer(@PathVariable(value = "id") Long freelancerId) throws Exception {
        try {
            return ResponseEntity.ok(freelancerService.getFreelancer(freelancerId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
