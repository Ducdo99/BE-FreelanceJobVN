package group5.freelancejob.controllers;

import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.models.PortfolioDto;
import group5.freelancejob.models.ReqUpdatePortfolioDto;
import group5.freelancejob.services.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PortfolioController {
    @Autowired
    private PortfolioService portfolioService;

    /**
     * @param freelancerId
     * @return
     * @throws Exception
     * @apiNote 6.02-get-portfolio
     */
    @GetMapping(value = "/portfolio/{FL-id}")
    public ResponseEntity<?> getPortfolio(@PathVariable(value = "FL-id") Long freelancerId) throws Exception {
        try {
            return portfolioService.getPortfolio(freelancerId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * @apiNote 6.03-update-portfolio
     */
    @PutMapping(path = "/portfolio/{FL-id}")
    public ResponseEntity<PortfolioDto> updatePortfolio(@PathVariable(name = "FL-id") Long flId, @RequestBody ReqUpdatePortfolioDto body) throws FJVNException {
        return ResponseEntity.ok(portfolioService.updatePortfolio(flId, body));
    }

    @PutMapping(path = "/portfolio-recruiter/{recruiter-id}")
    public ResponseEntity<?> updateRecruiter(@PathVariable(name = "recruiter-id") Long recruiterId, @RequestBody ReqUpdatePortfolioDto body) throws FJVNException {
        return ResponseEntity.ok(portfolioService.updateRecruiter(recruiterId, body));
    }

}
