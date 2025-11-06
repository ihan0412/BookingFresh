package est.oremi.backend12.bookingfresh.domain.product;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name")
    @Enumerated(EnumType.STRING)
    private CategoryName categoryName;

}
