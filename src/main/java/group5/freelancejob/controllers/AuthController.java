package group5.freelancejob.controllers;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import group5.freelancejob.daos.Account;
import group5.freelancejob.exception.ExistedUserException;
import group5.freelancejob.services.AccountService;
import group5.freelancejob.services.FreelancerService;
import group5.freelancejob.services.RecruiterService;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class AuthController {
    private final AccountService _accService;
    private final FreelancerService _freelancerService;
    private final RecruiterService _recruiterService;

    public AuthController(AccountService accService, FreelancerService freelancerService, RecruiterService recruiterService) {
        _accService = accService;
        _freelancerService = freelancerService;
        _recruiterService = recruiterService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthBody authBody) {
        try {
            String roleToCreate = authBody.getRole();
            FirebaseToken decodeToken = FirebaseAuth.getInstance().verifyIdToken(authBody.getIdToken());
            String email = decodeToken.getEmail();
            String name = decodeToken.getName();
            String avatar = decodeToken.getPicture();

            String newId = _accService.createAccount(email, avatar, roleToCreate, name);
            return ResponseEntity.ok(Collections.singletonMap("id", newId));
        } catch (ExistedUserException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("err_msg", ex.getMessage()));
        } catch (Exception ex){
            return ResponseEntity.internalServerError().body(Collections.singletonMap("err_msg", ex.getMessage()));
        }
    }
}
@Getter
class AuthBody {
    private String idToken;
    private String role;
}