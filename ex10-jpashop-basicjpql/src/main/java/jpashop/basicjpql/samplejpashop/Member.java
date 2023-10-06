package jpashop.basicjpql.samplejpashop;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MEMBER")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //기본값
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;

    //임베디드 타입으로 변경
    //private String city;
    //private String street;
    //private String zipcode;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") // ** @ManyToOne으로 관리되는 '필드'를 설정
    private List<Order> orders = new ArrayList<>(); //관례상 ArrayList로 초기화

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}