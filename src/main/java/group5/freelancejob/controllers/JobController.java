package group5.freelancejob.controllers;

import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.models.*;
import group5.freelancejob.services.JobService;
import group5.freelancejob.utils.JobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
public class JobController {
    @Autowired
    private JobService jobService;

    /**
     * @param pageNo
     * @param pageSize
     * @param jobStatus
     * @param jobTitle
     * @return
     * @throws Exception
     * @apiNote 3.01-get-all-jobs
     */
    @GetMapping(value = "/jobs")
    public ResponseEntity<?> getAllJobs(@RequestParam(value = "pageNo") int pageNo,
                                        @RequestParam(value = "pageSize") int pageSize,
                                        @RequestParam(value = "skill", required = false) List<Long> skillIds,
                                        @RequestParam(value = "status", required = false) JobStatus jobStatus,
                                        @RequestParam(value = "name", required = false) String jobTitle,
                                        @RequestParam(value = "genreId", required = false) Long genreId) throws Exception {
        try {
            return ResponseEntity.ok(jobService.findAllJobs(pageNo, pageSize, skillIds, jobStatus, jobTitle, genreId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(value = "/job/{job-id}")
    public ResponseEntity<RespJobDto> getJobById(@PathVariable(name = "job-id") Long jobId) {
        return ResponseEntity.ok(jobService.getJobById(jobId));
    }

    @GetMapping("/job-from-recruiter/{recruiter-id}")
    public ResponseEntity<?> getJobByRecruiterId(@PathVariable(name = "recruiter-id") Long recruiterId) throws FJVNException {
        return ResponseEntity.ok(jobService.getJobByRecruiterId(recruiterId));
    }

    /**
     * @param reqJob
     * @return
     * @throws Exception
     * @apiNote 3.03-create-new-job
     */
    @PostMapping(value = "/job")
    public ResponseEntity<?> createNewJob(@RequestBody @Valid NewJobReqDto reqJob) throws Exception {
        try {
            return jobService.createNewJob(reqJob);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * @param jobId
     * @param reqBody
     * @return
     * @throws Exception
     * @apiNote 3.04-update-job
     */
    @PutMapping(value = "/job/{id}")
    public HttpStatus updateAJob(@PathVariable(value = "id") Long jobId,
                                 @RequestBody UpdateJobReqDto reqBody) throws Exception {
        try {
            jobService.updateAJob(jobId, reqBody);
            return HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * @apiNote 3.04bis-update-job
     */
    @PutMapping(value = "/job/{id}/status/request-to-complete")
    public ResponseEntity<?> requestToCompleteJob(@PathVariable(value = "id") Long jobId,
                                                  @RequestBody RequestToCompleteJobDto request) throws Exception {
        jobService.requestToCompleteJob(jobId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/job/{id}/status/complete")
    public ResponseEntity<?> completeJob(@PathVariable(value = "id") Long jobId,
                                         @RequestBody UpdateCompleteJobDto request) throws Exception {
        try {
            jobService.updateCompleteJob(jobId, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("err_msg", e.getMessage()));
        }
    }

    /**
     * @apiNote 3.04bis2-update-job
     */
    @PutMapping(value = "/job/{id}/status/request-to-reopen")
    public ResponseEntity<?> requestToReopenJob(@PathVariable(value = "id") Long jobId,
                                                @RequestBody RequestToCompleteJobDto request) throws Exception {
        jobService.requestToReopenJob(jobId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * @param jobId
     * @return
     * @throws Exception
     * @apiNote 3.05-ban-job
     */
    @DeleteMapping(value = "/ban-job/{id}")
    public ResponseEntity<?> banAJob(@PathVariable(value = "id") Long jobId) throws Exception {
        try {
            jobService.banAJob(jobId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping(value = "/job/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long jobId) throws Exception {
        try {
            jobService.deleteJob(jobId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * jobs-per-month
     * DucDM
     *
     * @throws Exception
     */
    @GetMapping(value = "/job/jobs-per-month")
    public ResponseEntity<?> getJobsPerMonth() throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(jobService.getJobsPerMonth());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("err_msg", e.getMessage()));
        }
    }

    @GetMapping(value = "/job/count-finished-job")
    public ResponseEntity<?> countFinishedJobs() {
        try {
            return ResponseEntity.ok().body(jobService.countFinishedJobs());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("err_msg", ex.getMessage()));
        }
    }

    @GetMapping("/job/newest")
    public ResponseEntity<?> getNewestJob(){
        try {
            return ResponseEntity.ok().body(jobService.getTopTenJobs());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("err_msg", ex.getMessage()));
        }
    }

}
