package group5.freelancejob.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
public class FVNUserPrincipal implements UserDetails {
    private Long userId;
    private Long accountId;
    private String phone;
    private String email;
    private String roleName;
    private String fullName;
    private String avatar;
    private String desc;
    private String shortDesc;
    private Collection authorities;

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
