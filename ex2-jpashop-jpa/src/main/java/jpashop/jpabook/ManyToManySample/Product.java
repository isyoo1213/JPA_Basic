package jpashop.jpabook.ManyToManySample;

import javax.persistence.*;
import java.util.List;

@Entity
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PRODUCT_ID")
    private Long id;

    private String name;

    /**
     * '다대다' '양방향' 구성
     */
//    @ManyToMany(mappedBy = "products")
//    private List<MemberD> members = new ArrayList<>();

    //연결 테이블을 Entity로 승격한 경우 '다대다' -> '일대다'
    @OneToMany(mappedBy = "product")
    private List<MemberDProdjct> memberDProdjcts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
