package est.oremi.backend12.bookingfresh.domain.consumer.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    // private final Long id;
    private final String email;
    private final String password;

    @Getter
    private final Consumer consumer;


    public CustomUserDetails(Consumer consumer) {
        this.consumer = consumer;
        //this.id = consumer.getId();
        this.email = consumer.getEmail();
        this.password = consumer.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Long getId() {
        return consumer.getId();
    }
}
