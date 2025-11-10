package est.oremi.backend12.bookingfresh.domain.coupon.repository;

import est.oremi.backend12.bookingfresh.domain.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}