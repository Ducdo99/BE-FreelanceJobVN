package group5.freelancejob.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import group5.freelancejob.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class SecurityAuthenticateFilter extends OncePerRequestFilter {
    private final JwtUtil _jwtUtil;

    public SecurityAuthenticateFilter(JwtUtil jwtUtil) {
        _jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getServletPath().equals("/login")) {
            filterChain.doFilter(request, response);
        } else {
            String authoHeader = request.getHeader(AUTHORIZATION);
            if (authoHeader != null && authoHeader.startsWith("Bearer ")) {
                try {
                    verifyToken(authoHeader);
                    filterChain.doFilter(request, response);
                }catch (JWTVerificationException e){
                    Map<String,String> error = new HashMap<>();
                    error.put("err_msg",e.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    new ObjectMapper().writeValue(response.getOutputStream(),error);
                }
                catch (Exception e) {
                    response.setHeader("error",e.getMessage());
                    Map<String,String> error = new HashMap<>();
                    error.put("err_msg",e.getMessage());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(),error);
                }
            }else {
                filterChain.doFilter(request, response);
            }
        }
    }

    private void verifyToken(String authoHeader) {
        String token = authoHeader.substring("Bearer ".length());
        var decodedJWT = _jwtUtil.verifyJwt(token);
        String id = decodedJWT.getSubject();
        String role = decodedJWT.getClaim("role").asString();
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        var authenToken = new UsernamePasswordAuthenticationToken(id, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenToken);
    }
}
