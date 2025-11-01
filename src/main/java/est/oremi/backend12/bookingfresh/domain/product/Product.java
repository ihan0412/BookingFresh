package est.oremi.backend12.bookingfresh.domain.product;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)


  private Long id;
  private String name;
  private BigDecimal price;
  private int stock;
  private String weigt_pieces;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Photo> photos = new ArrayList<>();

  // 카테고리 연관관계
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categorie_idx", nullable = false)
  private Category category;
}