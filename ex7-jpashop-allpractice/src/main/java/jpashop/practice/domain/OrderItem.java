package jpashop.practice.domain;

import javax.persistence.*;

@Entity
@Table(name = "ORDERSITEM")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ORDERS_ITEMA_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ORDERS_ID")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "ITEMA_ID")
    private ItemA item;

    @Column(name = "ORDERS_PRICE")
    private int orderPrice;

    private int count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ItemA getItem() {
        return item;
    }

    public void setItem(ItemA item) {
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
