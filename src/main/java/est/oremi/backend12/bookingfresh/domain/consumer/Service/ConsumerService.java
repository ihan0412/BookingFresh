package est.oremi.backend12.bookingfresh.domain.consumer.Service;

import est.oremi.backend12.bookingfresh.domain.consumer.ConsumerRepository;
import est.oremi.backend12.bookingfresh.domain.consumer.dto.AddConsumerRequest;
import est.oremi.backend12.bookingfresh.domain.consumer.dto.ConsumerResponse;
import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConsumerService {
    private final ConsumerRepository consumerRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public ConsumerResponse signUp(AddConsumerRequest request) {
        // 비밀번호, 비밀번호 확인 일치 검사
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
        // 이메일 중복 확인
        if (consumerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일 입니다.");
        }
        // 닉네임 중복 확인
        if (consumerRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임 입니다.");
        }

        String encodedPassword = encoder.encode(request.getPassword());
        // Entity 생성 및 저장
        Consumer newConsumer = Consumer.builder()
                .email(request.getEmail())
                .password(encodedPassword) // 암호화된 비밀번호 저장
                .nickname(request.getNickname())
                .address(request.getAddress())
                .detailAddress(request.getDetailAddress())
                .createdAt(LocalDateTime.now())
                .build();

        consumerRepository.save(newConsumer);
        return new ConsumerResponse(newConsumer);
    }

    // todo: 로그인
}
