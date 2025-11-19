package est.oremi.backend12.bookingfresh.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "BookingFresh API 명세서",
                version = "v1.0.0",
                description = "신선식품 장보기 + AI 레시피/추천 서비스 백엔드 API 문서",
                contact = @Contact(
                        name = "BookingFresh GitHub",
                        url = "https://github.com/team-booking-fresh/BookingFresh"
                )
        )
)

public class SwaggerConfig {
}
