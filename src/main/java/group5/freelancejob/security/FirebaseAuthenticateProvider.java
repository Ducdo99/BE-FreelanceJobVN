package group5.freelancejob.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import group5.freelancejob.daos.Account;
import group5.freelancejob.exception.SecurityCustomException;
import group5.freelancejob.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class FirebaseAuthenticateProvider implements AuthenticationProvider {
    @Autowired
    private AccountService _accService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try{
            var cred = authentication.getPrincipal();
            FirebaseToken decodeToken = FirebaseAuth.getInstance().verifyIdToken((String) cred);
            Account fvnAcc = _accService.getAccountDetailFromEmail(decodeToken.getEmail());
            FVNUserPrincipal principal = _accService.getUserPrinciple(fvnAcc);
            return new UsernamePasswordAuthenticationToken(principal,null);
        }
        catch (Exception e){
            throw new SecurityCustomException(e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
