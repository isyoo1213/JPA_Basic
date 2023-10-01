package jpashop.jpainheritance.joinstrategy;

import javax.persistence.*;

@Entity
@Table(name = "ITEMH")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@DiscriminatorColumn()
/**
 * @DiscriminatorColumn
 * - 부모 엔터티의 테이블에 자식 테이블의 정보를 컬럼으로 표시해주는 방법
 * - name 속성으로 컬럼 이름 지정 가능
 * - 실제 컬럼에 들어가게 되는 값은 'entity의 이름'
 * - 자식 엔터티에 @DiscriminatorValue("값")을 통해서 Album -> A 이런 형식으로 사용하는 것도 가능
 * 장점
 * - ITEM, 즉 부모 테이블만 조회하더라도, 데이터의 자식 entity 유형을 파악할 수 있다
 */
public abstract class ItemH {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ITEMH_ID")
    private Long id;

    private String name;
    private int price;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
