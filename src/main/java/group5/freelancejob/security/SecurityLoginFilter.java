package group5.freelancejob.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import group5.freelancejob.utils.JwtUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class SecurityLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil _jwtUtil;
    public SecurityLoginFilter(@Lazy AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        setAuthenticationManager(authenticationManager);
        _jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String idToken = request.getParameter("idToken");
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(idToken, null));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        FVNUserPrincipal principal = (FVNUserPrincipal) authResult.getPrincipal();
        var jwt = _jwtUtil.createJwt(principal, request);

        Map<String, String> token = new HashMap<>();
        token.put("jwt", jwt);
        token.put("role", principal.getRoleName());
        token.put("accountId", principal.getAccountId().toString());
        token.put("phone", principal.getPhone());
        token.put("email", principal.getEmail());
        token.put("avatar", principal.getAvatar());
        if (!"admin".equals(principal.getRoleName())) {
            token.put("userId", principal.getUserId().toString());
            token.put("fullName", principal.getFullName());
            token.put("shortDesc", principal.getShortDesc());
            token.put("desc", principal.getDesc());
        }

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        Map<String, String> error = new HashMap<>();
        error.put("err_msg", authException.getLocalizedMessage());
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
