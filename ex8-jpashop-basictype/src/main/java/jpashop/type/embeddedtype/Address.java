package jpashop.type.embeddedtype;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Address {

    private String city;
    private String street;
    @Column(name = "ZIP_CODE")
    private String zipcode;

    /**
     * 임베디드 타입이 Entity를 들고있을 수 있다
     */
    //private PhoneNumber phoneNumber;

    /**
     * 실제로는 '기본 생성자' 포함되어있음을 꼭 인지!!
     */
    public Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }


    /**
     * 해당 임베디드 타입을 활용한 메서드 추가 가능 -> 응집성
     */

    public String getCity() {
        return city;
    }

    /**
     * 불변 객체를 위해 Setter 삭제
     */
//    public void setCity(String city) {
//        this.city = city;
//    }

    public String getStreet() {
        return street;
    }

    /**
     * 불변 객체를 위해 private으로 설정
     */
    private void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

//    public void setZipcode(String zipcode) {
//        this.zipcode = zipcode;
//    }

    /**
     * '동등성 비교'를 위한 equals() 재정의
     * - 기본 옵션만을 사용해서 생성함
     * - 실제 복잡한 다형성을 사용하는 경우에는 getter를 추가하는 옵션을 넣어야 할수도
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(city, address.city) && Objects.equals(street, address.street) && Objects.equals(zipcode, address.zipcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, zipcode);
    }
}
