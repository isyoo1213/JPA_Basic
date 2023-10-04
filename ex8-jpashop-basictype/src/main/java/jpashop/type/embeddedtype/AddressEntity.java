package jpashop.type.embeddedtype;

import javax.persistence.*;

@Entity
@Table(name = "ADDRESS_ENTITY")
public class AddressEntity {

    @Id @GeneratedValue
    @Column(name = "ADDRESS_ENTITY_ID")
    private Long id;

    private Address address; //빨간줄 뜨는건 무시

    public AddressEntity() {
    }

    public AddressEntity(String city, String street, String zipcode) {
        this.address = new Address(city, street, zipcode);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
