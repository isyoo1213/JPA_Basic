package jpashop.jpabook.domainV2;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MEMBERV2")
public class MemberV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //기본값
    @Column(name = "MEMBERV2_ID")
    private Long id;
    private String name;
    private String city;
    private String street;
    private String zipcode;

    /**
     * 객체 양방향 연관관계 매핑
     * 실제로는 비즈니스 상에서 주문 Table의 FK를 활용해 접근할 수 있으므로 멤버 -> 주문을 살펴볼 필요가 없음
     * 연습용 예제일 뿐
     */

    @OneToMany(mappedBy = "member") // ** @ManyToOne으로 관리되는 '필드'를 설정
    private List<OrderV2> orders = new ArrayList<>(); //관례상 ArrayList로 초기화

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}