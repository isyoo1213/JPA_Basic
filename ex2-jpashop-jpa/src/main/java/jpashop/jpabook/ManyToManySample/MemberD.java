package jpashop.jpabook.ManyToManySample;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MEMBER_D")
public class MemberD {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBER_D_ID")
    private Long id;

    /**
     * 1. '다대다' '단방향'
     * - @ManyToMany
     * - @JoinTable - name 속성을 통해 '다대일'을 형성해줄 중간 테이블의 이름 지정
     * -> 연결테이블에는 PK가 FK가 되는 구조로 풀어냄
     */
//    @ManyToMany
//    @JoinTable(name = "MEMBER_PRODUCT")
//    private List<Product> products = new ArrayList<>();

    //연결 테이블을 Entity로 승격한 경우 - '다대다' -> '일대다'
    @OneToMany(mappedBy = "member")
    private List<MemberDProdjct> memberProdjcts = new ArrayList<>();
}
