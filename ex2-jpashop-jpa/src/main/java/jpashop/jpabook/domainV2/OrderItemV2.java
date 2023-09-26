package jpashop.jpabook.domainV2;

import javax.persistence.*;

@Entity
@Table(name = "ORDERITEMV2")
public class OrderItemV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ORDER_ITEMV2_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ORDERV2_ID")
    private OrderV2 order;

    @ManyToOne
    @JoinColumn(name = "ITEMV2_ID")
    private ItemV2 item;

    private int orderPrice;

    private int count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderV2 getOrder() {
        return order;
    }

    public void setOrder(OrderV2 order) {
        this.order = order;
    }

    public ItemV2 getItem() {
        return item;
    }

    public void setItem(ItemV2 item) {
        this.item = item;
    }

    public int getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
