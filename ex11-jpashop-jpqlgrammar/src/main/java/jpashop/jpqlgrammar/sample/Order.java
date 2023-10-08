package jpashop.jpqlgrammar.sample;

import javax.persistence.*;

@Entity
@Table(name = "ORDERS") //예약어 피하기!!
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ORDER_ID")
    private Long id;

    private int orderAmount;

    @Embedded
    private Address address;

    /**
     * '다대일' '단방향'
     * - Product에서는 Order를 살펴볼 일이 없다고 판단하므로 이렇게 구성함
     */
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
