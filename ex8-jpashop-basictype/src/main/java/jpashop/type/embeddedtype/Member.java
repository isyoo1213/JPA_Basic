package jpashop.type.embeddedtype;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /**
     * 값 타입 컬렉션
     * - @ElementCollection 으로 명시
     * - @CollectionTable 으로 Table 이름 및 FK 지정
     *   1. name 속성 - Table 이름
     *   2. joinColumns = @JoinColumn(name = "FK 이름") - FK 지정
     * - 임베디드 타입인 경우, 내부 필드에서 column 명들을 지정할 수 있음 or @AttributeOverrides
     * - *** 임베디드 타입이 아닌 경우, column이 하나일 경우 @Column을 통해 column 이름 지정
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "FAVORITE_FOOD", joinColumns = @JoinColumn(name = "MEMBER_ID"))
    @Column(name = "FOOD_NAME")
    private Set<String> favoriteFoods = new HashSet<>();

// 값 타입 컬렉션으로 관리하는 방법 -> 결국 제약사항 생김
//    @ElementCollection(fetch = FetchType.LAZY)
//    @CollectionTable(name = "ADDRESS_HISTORY", joinColumns = @JoinColumn(name = "MEMBER_ID"))
//    private List<Address> addressHistory = new ArrayList<>();

    //1. '일대다' '단방향' + 영속성 전이 + 고아객체 삭제로 풀어가는 방법
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID")
    private List<AddressEntity> addressHistory = new ArrayList<>();

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

    public Address getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(Address workAddress) {
        this.workAddress = workAddress;
    }

    public Set<String> getFavoriteFoods() {
        return favoriteFoods;
    }

    public void setFavoriteFoods(Set<String> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

//    public List<Address> getAddressHistory() {
//        return addressHistory;
//    }
//
//    public void setAddressHistory(List<Address> addressHistory) {
//        this.addressHistory = addressHistory;
//    }

    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
    }
}