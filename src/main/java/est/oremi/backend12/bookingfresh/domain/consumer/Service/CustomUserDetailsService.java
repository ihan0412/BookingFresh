package est.oremi.backend12.bookingfresh.domain.consumer.Service;

import est.oremi.backend12.bookingfresh.domain.consumer.ConsumerRepository;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailsService implements UserDetailsService {
    private final ConsumerRepository consumerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // email로 사용자 조회
        Consumer consumer = consumerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email));
        return new CustomUserDetails(consumer);
    }
}
