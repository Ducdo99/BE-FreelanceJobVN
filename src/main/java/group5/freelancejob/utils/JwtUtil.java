package group5.freelancejob.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import group5.freelancejob.security.FVNUserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secrekt}")
    private String JWT_SECREKT;

    public String createJwt(FVNUserPrincipal principal, HttpServletRequest request) {
        Algorithm algo = Algorithm.HMAC256(JWT_SECREKT);
        String jwt = JWT.create().withSubject(String.valueOf(principal.getUserId()))
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("role", principal.getRoleName())
                .withClaim("email",principal.getEmail())
                .withClaim("phone",principal.getPhone())
                .sign(algo);
        return jwt;
    }

    public DecodedJWT verifyJwt(String token){
        Algorithm algo = Algorithm.HMAC256(JWT_SECREKT);
        JWTVerifier verifier = JWT.require(algo).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT;
    }
}
