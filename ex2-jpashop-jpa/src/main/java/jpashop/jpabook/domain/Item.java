package jpashop.jpabook.domain;

import javax.persistence.*;

/**
 * *** 현재 Entity 연관관계 설정의 한계점
 * '객체 설계'를 'DB 테이블 설계'에 맞춘 방식
 * 테이블의 FK를 객체에 '그대로' 가져옴
 * ex) OrderItem 클래스가 Order/Item의 '객체'가 아닌 '순수한' PK 값을 그대로 필드로 들고있음
 * -> 객체 그래프 탐색 불가능
 * 참조가 없으므로 UML도 실제로는 참조가 끊긴 설계
 */
@Entity
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ITEM_ID") // 최근은 소문자 + 언더스코어 방식을 많이 활용함
    private Long id;

    // *** Column 명 같은 경우는, 그냥 회사 관례를 따르는 것. 아직은 언더스코어 사용이 관례.
    // ex) java 엔터티는 카멜케이스 + DB 테이블 컬럼명은 대문자 + 언더스코어
    // + 최근 DB들은 대소문자 구분하기도 하므로 그냥 관례 따라.
    // *** SpringBoot를 통해 hibernate/jpa 사용한다면, JAVA의 카멜케이스를 기본으로 '소문자+언더스코어' 조합으로 변환함
    // -> 여튼 필요에 따라 Entity와 Table Column 명의 매핑정보는 유연하게 가져되, '표시해주는 것이 안전'

    // *** 테이블 생성 시, 컬럼 변수명 뒤의 메타데이터, ex) varchar(255) 표시는, 개발에서는 그냥 사용하고 운영 단계에서 DB 스크립트 반영할 때 수정할 수도 있음
    // + Column 속성으로 숫자를 직접 주는 방식도 있음 ex) @Column(length = 10)
    // + 윗 줄 처럼 Entity 클래스에 직접 표기하는 것을 추천하긴 함
    // + @Table(index = ... ) 등도 직접 표기해놓는 것이, 다른 개발자들이 JPQL로 쿼리를 짤 때 테이블까지 보지 않고 Entity만으로 참고할 수 있음
    private String name;
    private int price;
    private int stockQuantity;

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

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
