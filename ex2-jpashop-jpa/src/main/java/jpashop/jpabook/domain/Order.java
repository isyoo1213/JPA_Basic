package jpashop.jpabook.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ORDERS") // *** DB마다 다르지만, order 문자열 자체가 예약어로 사용될 수 있으니 table 이름은 orders로 사용하는 것을 추천
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ORDER_ID")
    private Long id;

    @Column(name = "MEMBER_ID")
    private Long memberId;

    private LocalDateTime orderDate; //최근의 hibernate 버전은 java8 이후의 LocalDate/LocalDateTime을 자동으로 매핑해줌

    @Enumerated(EnumType.STRING) // *** enum 타입은 ORDINAL X / STRING O -> ORDINAL의 순서 꼬임 방지
    private OrderStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
