package jpashop.type.embeddedtype;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MEMBER")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //기본값
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    //기간 Period
    //기존
    //private LocalDateTime startDate;
    //private LocalDateTime endDate;

    //임베디드 타입
    @Embedded
    private Period wokrPeriod;

    //주소 Address
    //기존
    //private String city;
    //private String street;
    //private String zipcode;

    //임베디드 타입
    @Embedded
    private Address homeAddress;

    /**
     * 같은 Entity 내에서 임베디드 타입을 2번 이상 사용하기
     * - @AttributeOverride
     */

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city",
                    column = @Column(name = "WORK_CITY")),
            @AttributeOverride(name = "street",
                    column = @Column(name = "WORK_STREET")),
            @AttributeOverride(name = "zipcode",
                    column = @Column(name = "WORK_ZIPCODE"))}
    )
    private Address workAddress;

    /**
     * 생성되는 Table자체에 변화는 없음
     */

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

    public Period getWokrPeriod() {
        return wokrPeriod;
    }

    public void setWokrPeriod(Period wokrPeriod) {
        this.wokrPeriod = wokrPeriod;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }
}