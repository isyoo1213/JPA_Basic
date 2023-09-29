package jpashop.jpabook.manytomanySample;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MEMBER_D_PRODUCT") // 실제 Entity이름은 연결 테이블 이름보단 비즈니스 의미가 담긴 이름으로 주로 명명
public class MemberDProdjct {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBER_D_PRODUCT_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_D_ID")
    private MemberD member;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    // *** 연결 테이블을 Entity로 승격시키면, 추가적인 필드를 통해 정보 사용 가능
    private int count;
    private int price;
    private LocalDateTime orderDateTime;
}
