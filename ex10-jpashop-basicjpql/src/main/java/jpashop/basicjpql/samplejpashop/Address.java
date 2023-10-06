package jpashop.basicjpql.samplejpashop;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Address {

    @Column(length = 10)
    private String city;
    @Column(length = 20)
    private String street;
    @Column(length = 5)
    private String zipcode;

    /**
     * Embeded Type 생성
     * 1. Setter는 private으로 막기
     * 2. equals() / hashCode() 생성
     *    -> *** use getters during code generation 옵션 체크
     *    -> 1. 필드에 직접 접근하지 않고 getter로 접근하므로 안전
     *       2. *** Proxy일 때에는 접근이 불가함 but, JPA에서는 getter로 접근 시, 진짜 객체를 찾아가는 로직이 있음
     */

    /**
     * 값 타입을 사용하는 것의 장점
     * - 의미있는 메서드를 응집도있게 만들 수 있음
     *   ex) validation 룰을 공통으로 사용하거나, getter를 통한 조합 등
     * - Column의 정보 설정 + 공통 관리
     *   ex) length
     */
    public String getFullAddress() {
        return getCity() + " " + getStreet() + " " + getZipcode();
    }

    public boolean isValid() {
        //validation 로직
        return true;
    }

    public String getCity() {
        return city;
    }

    private void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    private void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    private void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(getCity(), address.getCity()) && Objects.equals(getStreet(), address.getStreet()) && Objects.equals(getZipcode(), address.getZipcode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCity(), getStreet(), getZipcode());
    }
}
