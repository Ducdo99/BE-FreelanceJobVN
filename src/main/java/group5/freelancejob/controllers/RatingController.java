package group5.freelancejob.controllers;

import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.models.RequestRateJobDto;
import group5.freelancejob.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rating")
public class RatingController {
    @Autowired
    private RatingService ratingService;

    @PutMapping(path = "/job/{jobId}")
    public ResponseEntity<?> rateJob(@PathVariable(name = "jobId") Long jobId, @RequestBody RequestRateJobDto requestRateJobDto) throws FJVNException {
        return ResponseEntity.ok(ratingService.rateJob(jobId, requestRateJobDto));
    }
}
