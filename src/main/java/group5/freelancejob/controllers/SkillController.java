package group5.freelancejob.controllers;

import group5.freelancejob.services.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkillController {
    @Autowired
    private SkillService skillService;

    /**
     * @return
     * @throws Exception
     * @apiNote 10.01-get-skills
     * @author DucDM
     * @implNote rewrited by LamHNT
     */
    @GetMapping(value = "/skills")
    public ResponseEntity<?> getSkills() throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(skillService.getSkills());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
