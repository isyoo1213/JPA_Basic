package jpashop.type.embeddedtype;

import javax.persistence.*;

@Entity
@Table(name = "PHONE_NUMBER")
public class PhoneNumber {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PHONE_NUMBER_ID")
    private Long id;

    private String phonenumber;
}
