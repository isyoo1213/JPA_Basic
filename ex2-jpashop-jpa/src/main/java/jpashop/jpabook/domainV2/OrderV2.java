package jpashop.jpabook.domainV2;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERV2")
public class OrderV2 {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ORDERV2_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBERV2_ID")
    private MemberV2 member;

    private LocalDateTime orderDate;

    private OrderStatusV2 status;

    /**
     * 비즈니스적으로 의미가 있으므로 충분히 객체 양방향 연관관계 설정해줄만함
     */
    @OneToMany(mappedBy = "order")
    private List<OrderItemV2> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItemV2 orderItemV2) {
        orderItems.add(orderItemV2);
        orderItemV2.setOrder(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MemberV2 getMember() {
        return member;
    }

    public void setMember(MemberV2 member) {
        this.member = member;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatusV2 getStatus() {
        return status;
    }

    public void setStatus(OrderStatusV2 status) {
        this.status = status;
    }


}
