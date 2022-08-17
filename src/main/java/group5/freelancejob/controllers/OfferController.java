package group5.freelancejob.controllers;

import group5.freelancejob.exception.FJVNException;
import group5.freelancejob.models.CreateOfferRequest;
import group5.freelancejob.models.OfferDto;
import group5.freelancejob.models.OfferWithJobDto;
import group5.freelancejob.models.offerbyjobid.OfferByJobIdDto;
import group5.freelancejob.services.OfferService;
import group5.freelancejob.utils.OfferStatus;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;

@RestController

public class OfferController {

    @Autowired
    private OfferService offerService;

    /**
     * @param jobId
     * @return
     * @throws FJVNException
     * @apiNote 5.04-get-all-offers (by jobId)
     */
    @GetMapping(value = "/offer/{job-id}")
    public ResponseEntity<OfferByJobIdDto> getOffersByJobId(@PathVariable(name = "job-id") Long jobId) throws FJVNException {
        return ResponseEntity.ok(offerService.getOffersByJobId(jobId));
    }

    @GetMapping(value = "/offer-freelancer/{freelancer-id}")
    public ResponseEntity<List<OfferWithJobDto>> getOffersByFreelancerId(@PathVariable(name = "freelancer-id") Long freelancerId) throws FJVNException {
        return ResponseEntity.ok(offerService.getOffersByFreelancerId(freelancerId));
    }

    @GetMapping(value = "/offer-freelancer-job/{freelancer-id}/{job-id}")
    public ResponseEntity<OfferDto> getOfferByFreelancerIdAndJobId(@PathVariable(name = "freelancer-id") Long freelancerId, @PathVariable(name = "job-id") Long jobId) throws FJVNException {
        return ResponseEntity.ok(offerService.getOfferByFreelancerIdAndJobId(freelancerId, jobId));
    }

    /**
     * @param jobId
     * @param reqBody
     * @return
     * @throws Exception
     * @author LamHNT
     * @apiNote 5.01-create-offer
     */
    @PostMapping(value = "/offer/{job-id}")
    public ResponseEntity<?> createNewOffer(@PathVariable(value = "job-id") Long jobId,
                                            @RequestBody CreateOfferRequest reqBody) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap("offerId", offerService.createNewOffer(jobId, reqBody)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("err_msg", e.getMessage()));
        }
    }

    /**
     * @param offerId
     * @param offerRequest
     * @return
     * @apiNote 5.05-update-offer
     */
    @PutMapping("/offer/status/{offer-id}")
    public ResponseEntity<?> acceptOffer(@PathVariable(value = "offer-id") Long offerId, @RequestBody OfferRequest offerRequest) {
        try {
            offerService.updateOfferStatus(offerId, offerRequest.getJobId(), offerRequest.getStatus());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * @param id
     * @param offerObject
     * @return
     * @throws Exception
     * @apiNote 5.02 update offer
     * @author DucDM
     */
    @PutMapping(value = "/offer/{offer-id}")
    public ResponseEntity<?> updateOffer(@PathVariable(value = "offer-id") Long id, @RequestBody OfferDto offerObject) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(offerService.updateOffer(id, offerObject));
        } catch (NumberFormatException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    /**
     * @apiNote 5.03-delete-offer
     */
    @DeleteMapping(value = "/offer/{offer-id}")
    public ResponseEntity<?> deleteOffer(@PathVariable(value = "offer-id") Long id) throws Exception {
        try {
            offerService.deleteOffer(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NumberFormatException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("err_msg", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("err_msg", ex.getMessage()));
        }
    }

    /**
     * @return
     * @throws Exception
     * @apiNote LamHNT-count-total-offers
     */
    @GetMapping(value = "/offer/total")
    public ResponseEntity<?> countTotalOffers() throws Exception {
        try {
            return offerService.getTotalOffers();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("err_msg", e.getMessage()));
        }
    }

    /**
     * @return
     * @throws Exception
     * @apiNote LamHNT_count_final_rejected_offers
     */
    @GetMapping(value = "/offer/rejected")
    public ResponseEntity<?> countTotalRejectedOffers() throws Exception {
        try {
            return offerService.getTotalRejectedOffers();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("err_msg", e.getMessage()));
        }
    }
}

@Getter
class OfferRequest {
    private OfferStatus status;
    private Long jobId;
}

