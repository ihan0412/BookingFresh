package est.oremi.backend12.bookingfresh.domain.coupon.service;

import est.oremi.backend12.bookingfresh.domain.consumer.entity.Consumer;
import est.oremi.backend12.bookingfresh.domain.coupon.CategoryCoupon;
import est.oremi.backend12.bookingfresh.domain.coupon.Coupon;
import est.oremi.backend12.bookingfresh.domain.coupon.dto.NewCouponRegisteredEvent;
import est.oremi.backend12.bookingfresh.domain.coupon.UserCoupon;
import est.oremi.backend12.bookingfresh.domain.coupon.dto.CategoryInfo;
import est.oremi.backend12.bookingfresh.domain.coupon.dto.CouponRegistrationRequest;
import est.oremi.backend12.bookingfresh.domain.coupon.dto.CouponResponse;
import est.oremi.backend12.bookingfresh.domain.coupon.dto.UserCouponResponse;
import est.oremi.backend12.bookingfresh.domain.coupon.repository.CategoryCouponRepository;
import est.oremi.backend12.bookingfresh.domain.coupon.repository.CategoryRepository;
import est.oremi.backend12.bookingfresh.domain.coupon.repository.CouponRepository;
import est.oremi.backend12.bookingfresh.domain.coupon.repository.UserCouponRepository;
import est.oremi.backend12.bookingfresh.domain.product.Category;

import est.oremi.backend12.bookingfresh.domain.product.Product;
import est.oremi.backend12.bookingfresh.domain.product.ProductRepository;
import est.oremi.backend12.bookingfresh.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryCouponRepository categoryCouponRepository;
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행 도구

    @Transactional
    public Coupon registerNewCoupon(CouponRegistrationRequest request) {
        Boolean isActiveValue = request.getIsActive() != null ? request.getIsActive() : true; // null 체크
        // Coupon 엔티티 생성 및 저장
        Coupon newCoupon = Coupon.builder()
                .code(request.getCode())
                .name(request.getName())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount())
                .isActive(isActiveValue)
                .build();

        Coupon savedCoupon = couponRepository.save(newCoupon);

        // 카테고리 연관관계 매핑 (CategoryCoupon 생성 및 저장)
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            // 유효성 검사 (ID는 요청했는데 해당 카테고리가 없는 경우)
            if (categories.size() != request.getCategoryIds().size()) {
                throw new IllegalArgumentException("요청된 카테고리 ID 중 존재하지 않는 ID가 포함되어 있습니다.");
            }

            // CategoryCoupon 객체 리스트 생성
            List<CategoryCoupon> categoryCoupons = categories.stream()
                    .map(category -> CategoryCoupon.builder()
                            .coupon(savedCoupon)
                            .category(category)
                            .build())
                    .toList();

            categoryCouponRepository.saveAll(categoryCoupons);
        }

        // 이벤트 발행: 쿠폰 등록 트랜잭션이 성공적으로 완료될 때, 쿠폰 발급을 위한 이벤트 발행
        System.out.println("새 쿠폰 '" + savedCoupon.getName() + "' 등록 완료. 발급 이벤트를 발행합니다.");
        eventPublisher.publishEvent(new NewCouponRegisteredEvent(savedCoupon));

        return savedCoupon;
    }

    // 쿠폰 목록과 해당 쿠폰의 적용 카테고리들 조회
    @Transactional(readOnly = true)
    public List<CouponResponse> findAllCouponsWithCategories() {
        // 적용가능한 카테고리 정보를 포함한 모든 Coupon 엔티티 조회
        List<Coupon> allCoupons = couponRepository.findAllWithCategories();

        return allCoupons.stream()
                .map(coupon -> {
                    List<CategoryInfo> categories = coupon.getCategoryCoupons().stream()
                            .map(CategoryCoupon::getCategory)
                            .map(CategoryInfo::from) // Category 엔티티를 CategoryInfo DTO로 변환
                            .toList();

                    // 최종 DTO 생성 및 반환
                    return CouponResponse.from(coupon, categories);
                })
                .toList();
    }

    // 사용자가 사용 가능한 모든 쿠폰 조회
    @Transactional(readOnly = true)
    public List<UserCouponResponse> findAvailableUserCoupons(Long consumerId) {
        // 해당 사용자의 모든 UserCoupon 조회 (Coupon 정보 패치 조인)
        List<UserCoupon> userCoupons = userCouponRepository.findByConsumerIdWithCoupon(consumerId);

        return userCoupons.stream()
                // 사용 가능한 쿠폰만 필터링 (사용하지 않았고, 쿠폰 자체가 활성화된 경우)
                .filter(userCoupon -> !userCoupon.getIsUsed() && userCoupon.getCoupon().getIsActive())

                .map(userCoupon -> {
                    Coupon coupon = userCoupon.getCoupon();

                    // 해당 쿠폰에 매핑된 CategoryCoupon 목록 조회
                    // Todo: (N+1이 발생할 수 있으나, 현재 구현 구조상 분리)
                    List<CategoryCoupon> categoryCoupons = categoryCouponRepository.findByCouponId(coupon.getId());

                    // Category 엔티티를 DTO로 변환하고 중복 제거
                    List<CategoryInfo> categories = categoryCoupons.stream()
                            .map(CategoryCoupon::getCategory)
                            .map(CategoryInfo::from)
                            .distinct()
                            .toList();

                    // 최종 UserCouponResponse DTO 생성
                    return UserCouponResponse.from(userCoupon, categories);
                })
                .toList();
    }

    @Transactional
    public void issueAllActiveCouponsToNewConsumer(Consumer newConsumer) {
        // 현재 DB에 존재하는 모든 활성 쿠폰 조회
        List<Coupon> activeCoupons = couponRepository.findByIsActive(true);

        if (activeCoupons.isEmpty()) {
            System.out.println("발행 가능한 활성 쿠폰이 없어 회원 ID " + newConsumer.getId() + "에게 지급할 쿠폰이 없습니다.");
            return;
        }

        // 각 쿠폰에 대해 UserCoupon 엔티티 생성
        List<UserCoupon> userCouponsToSave = activeCoupons.stream()
                .map(coupon -> UserCoupon.builder()
                        .consumer(newConsumer)
                        .coupon(coupon)
                        .build())
                .toList();

        // UserCoupon 저장 (새 사용자에게 쿠폰 발급)
        userCouponRepository.saveAll(userCouponsToSave);

        System.out.println("회원 ID " + newConsumer.getId() + "에게 " + userCouponsToSave.size() + "개의 초기 쿠폰이 발급되었습니다.");
    }

    private final ProductRepository productRepository;
    // 사용자가 소유하고 있으며, 상품에 적용 가능(카테고리)한 모든 쿠폰 조회
    @Transactional(readOnly = true)
    public List<UserCouponResponse> findApplicableCouponsForUserAndProduct(Long consumerId, Long productId) {

        // 상품 ID로 카테고리 ID를 추출
        Product product = productRepository.findByIdWithCategory(productId)
                .orElseThrow(() -> new NotFoundException("상품 ID " + productId + "를 찾을 수 없습니다."));

        Long productCategoryId = product.getCategory().getId();

        // 해당 사용자의 모든 유효한 UserCoupon 조회
        List<UserCoupon> userCoupons = userCouponRepository.findByConsumerIdWithCoupon(consumerId);

        // 필터링 및 최종 DTO 변환
        return userCoupons.stream()
                // 사용하지 않았고, 쿠폰 자체가 활성화된 경우만 필터링
                .filter(uc -> !uc.getIsUsed() && uc.getCoupon().getIsActive())
                // 해당 쿠폰이 현재 상품의 카테고리에 적용 가능한지 확인
                .filter(uc -> isCouponApplicableToCategory(uc.getCoupon().getId(), productCategoryId))
                // DTO 변환
                .map(uc -> convertUserCouponToResponse(uc))
                .toList();
    }


    // 쿠폰이 카테고리에 적용 가능한지 확인하는 도우미 메소드
    @Transactional(readOnly = true)
    protected boolean isCouponApplicableToCategory(Long couponId, Long categoryId) {
        // 해당 쿠폰 ID에 매핑된 모든 CategoryCoupon 조회
        List<CategoryCoupon> mappings = categoryCouponRepository.findByCouponId(couponId);

        // 조회된 매핑 목록에 현재 상품의 카테고리 ID가 포함되어 있는지 확인
        return mappings.stream()
                .anyMatch(mapping -> mapping.getCategory().getId().equals(categoryId));
    }

    // UserCoupon을 UserCouponResponse DTO로 변환
    @Transactional(readOnly = true)
    protected UserCouponResponse convertUserCouponToResponse(UserCoupon userCoupon) {
        Coupon coupon = userCoupon.getCoupon();

        // 이 쿠폰이 적용 가능한 '전체' 카테고리 목록 조회
        List<CategoryCoupon> allMappings = categoryCouponRepository.findByCouponId(coupon.getId());

        List<CategoryInfo> categories = allMappings.stream()
                .map(CategoryCoupon::getCategory)
                .map(CategoryInfo::from)
                .distinct()
                .toList();

        return UserCouponResponse.from(userCoupon, categories);
    }
}