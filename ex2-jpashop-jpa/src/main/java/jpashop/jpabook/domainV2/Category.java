package jpashop.jpabook.domainV2;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CATEGORY_ID")
    private Long id;

    private String name;

    /**
     * * tree 처럼 내려가는 카테고리를 셀프로 구성하는 방식
     */
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> childs = new ArrayList<>();

    /**
     * *** '다대다'를 객체로 '양방향'으로 구성해주는 방식
     * - 실제로는 JPA가 생성하는 '연결 테이블'의 이름과 컬럼, FK를 매핑해주는 방식
     * - @JoinTable의 속성
     * 1. name - 연결 테이블의 이름
     * 2. joinColumns - 현재 조인하는 Entity의 PK -> 연결 테이블의 FK로 삽입됨
     * 3. inverseJoinColumns - 반대쪽 조인하는 상대 Entity의 PK -> 연결 테이블의 FK로 삽입됨
     * ***** '다대다''양방향' 연관관계 매핑에도 '연관관계의 주인'은 설정해주어야 한다!
     */
    @ManyToMany
    @JoinTable(name = "CATEGORY_ITEM",
    joinColumns = @JoinColumn(name = "CATEGORY_ID"),
    inverseJoinColumns = @JoinColumn(name = "ITEMV2_ID"))
    private List<ItemV2> items;
}
