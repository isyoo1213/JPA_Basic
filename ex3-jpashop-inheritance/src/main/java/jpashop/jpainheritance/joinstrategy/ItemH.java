package jpashop.jpainheritance.joinstrategy;

import javax.persistence.*;

@Entity
@Table(name = "ITEMH")
@Inheritance(strategy = InheritanceType.JOINED)
public class ItemH {

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
