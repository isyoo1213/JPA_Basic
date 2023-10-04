package jpashop.type;

import jpashop.type.embeddedtype.Address;
import jpashop.type.embeddedtype.Member;
import jpashop.type.embeddedtype.Period;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-basic");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {

            /**
             * JPA의 데이터 타입 분류
             * 1. Entity 타입
             * - @Entity로 정의하는 객체
             * - 내부 데이터가 변해도 가지고있는 '식별자'로 지속해서 '추적 가능'
             *   ex) 회원 Entity의 키나 나이를 변경해도, 가지고있는 '식별자'로 인식 가능
             * 2. 값 타입
             * - int/Integer/String 처럼 단순히 값으로 사용하는 JAVA primative/Wrapper/String type
             * - 식별자가 없고, 값만 있으므로 '추적 불가'
             *   ex)숫자 100을 200으로 변경하면 완전히 다른 값으로 대체
             *   ex) Order라는 Entity는 id 식별자를 가지고 있으므로 추적가능하지만, contents라는 column은 값이 변하면 추적 불가능
             * 특징
             * - 생명 주기를 Entity에 의존
             *   ex) 회원 Entity를 삭제하면, 그 내부의 String name/int age 모두 삭제
             * - *** 값 타입은 공유되면 안됨
             *   ex) 회원 이름 변경시, 다른 회원의 이름도 함께 변경되면 안됨
             *
             */

            /**
             * 참고 ***
             * JAVA의 Primative 타입은 절대 공유 X !!!!!
             * - 기본 타입은 항상 값을 '복사'함 -> stack에 참조가 아닌 '값'그 자체를 복사에서 load함
             * - Wrapper/String 클래스 -> 참조를 끌고 와버림 -> '공유'가 가능해져버림 *** -> 서로 다른 값으로 변경이 일어날 가능성 X
             *   + but, 공유하는 인스턴스간 서로 같은 '값'자체의 의도된 변경이 아니라, 의도치 않게 공유되는 것 자체는 문제 가능
             */
            int a = 10;
            int b = a;
            a = 20;
            System.out.println("a = " + a);
            System.out.println("b = " + b);

            Integer c = new Integer(10);
            System.out.println("c.getClass().hashCode() = " + c.getClass().hashCode());
            Integer d = c;
            System.out.println("d.getClass().hashCode() = " + d.getClass().hashCode());
            // 만약, c의 값을 변경하는 메서드를 통해 값을 변경해야 하는 경우
            c = 20;
            // 이것은 String의 immutable한 성격과 유사한 Integer만의 immutable한 특성이므로 적절한 예시 X
            System.out.println("c = " + c); //20
            System.out.println("d = " + d); //10

            /**
             * 값 타입의 종류
             * 1. Primative 타입
             * - 자바 기본 primative 타입 (int, double)
             * - Wrapper 클래스 (Integer, Long)
             * - String
             * ---------------------------- 아래는 JPA에서 정의한 타입
             * 2. 임베디드 타입 (복합 값 타입)
             * - ex) 좌표 - (x, y) - 두 정보를 묶어서 쓰는 경우 -> Position이라는 클래스로 구성 -> custom한 Position을 '값'처럼 쓰고 싶을 때
             * 3. 컬렉션 값 타입
             * - 자바 collection에 위의 두 타입을 담아서 사용할 수 있는 타입
             */

            /**
             * 임베디드(내장) 값 타입
             * - 새로운 값 타입을 정의할 수 있음
             * - JPA는 임베디드 타입(embedded type)이라 함
             * - 주로 기본 타입을 모아서 만들어 '복합 값 타입(Composited value type)'이라고도 함
             * - *** Primative/Wrapper/String과 같은 '값 타입' -> '추적 불가' '변경되면 끝'
             */

            /**
             * 임베디드 타입 사용법
             * @Embeddable - '값 타입'을 '정의하는 곳'에 표시
             * @Embedded - '값 타입'을 '사용하는 곳'에 표시
             * + *** 기본 생성자 필수
             */

            /**
             * 임베디드 타입의 장점
             * - 시스템 전체에서 재사용 가능
             * - 높은 응집도
             * - Period.isWork() 처럼 해당 '값 타입'만 사용하는 의미있는 메서드 구현 가능 -> 객체지향적 설계 가능
             * - 임베디드 타입을 포함한 모든 '값 타입'은, '값 타입'을 '소유한 Entity'의 생명주기에 의존함
             *   -> '값 타입'이라는 것은, 당연스럽게 Entity 생성/소멸의 라이프 사이클을 의존함
             */

            /**
             * 임베디드 타입과 '테이블 매핑'
             * - Table관점 : Table 자체는 변동 없음
             * - 객체 관점 : Entity가 임베디드 값 타입을 소유하게끔 구성해주면 끝
             * -> Table은 '저장' / 객체는 클래스나 메서드를 통한 '기능 + 조작'의 추가적인 효용이 있어야함
             * -> 즉, DB와 객체의 패러다임 차이를 매핑
             * 특징
             * - 임베디드 타입은 'Entity'의 '값'일 뿐이다
             * - *** 사용 전/후의 매핑하는 'Table'은 변화가 없다
             * - '객체'와 'Table'을 아주 세밀하게(find-grained) 매핑하는 것이 가능하다
             *    + '모델링에 있어 필드의 축소 및 깔끔함' + '응집성'의 장점
             * - 잘 설계된 ORM 애플리케이션은 '매핑한 Table'의 수보다 '클래스'의 수가 더 많다
             *   -> 값 타입들을 적절하게 잘 활용한다는 의미
             * - Entity의 임베디드 타입 필드가 null이면, 매핑된 임베디드 타입 클래스의 필드 모두 null로 세팅됨
             */

            Address address1 = new Address("city1", "street1", "zipcode1");

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setHomeAddress(address1);
            member1.setWokrPeriod(
                    new Period(LocalDateTime.of(2023, 10, 3, 12, 0),
                            LocalDateTime.of(2023, 10, 5, 12, 0)));

            em.persist(member1);

            /**
             * 임베디드 타입과 '연관관계'
             * - 임베디드 타입 클래스도 FK 필드를 통해 Entity를 가질 수 있다
             * - ex) Address 클래스 내에 PhoneNumber Entity를 필드로 가지기
             * - *** 대신 연관관계 매핑 해주어야 함
             */

            /**
             * @AttributeOverrides / @AttributeOverride
             * 만약, 한 Entity 내에서 동일한 임베디드 타입 필드를 2개 이상 가진다면?
             * - ex) private Address homeAddress / private Address workAddress
             * - 컬럼명이 중복되지 않도록 처리해주어야함 (잘 안씀)
             * @Embedded
             *     @AttributeOverrides({
             *             @AttributeOverride(name = "city",
             *                     column = @Column(name = "WORK_CITY")),
             *             @AttributeOverride(name = "street",
             *                     column = @Column(name = "WORK_STREET")),
             *             @AttributeOverride(name = "zipcode",
             *                     column = @Column(name = "WORK_ZIPCODE"))}
             *     )
             *     private Address workAddress;
             * -> 실제 Table에는 해당 Column들이 새로 추가된다
             */

            /**
             * 값 타입 공유참조
             * - 임베디드 타입 같은 '값 타입'을 여러 entity에서 공유하면 위험
             * - sideEffect 발생
             * 해결
             * 1. 공유가 필요할 경우, Entity 타입을 사용할 것
             * - 값 타입은 sideEffect 발생하면 절대 안됨
             *   -> 값 타입이 아닌, Entity 타입으로 공유해야함
             * 2. 값 타입을 통해 공유해야 하는 경우, 값 타입의 인스턴스를 '복사'해서 사용
             *   -> 기존 값 타입의 인스턴스가 아닌, 새로운 인스턴스에 값을 '복사'해서 사용
             *   *** 위험성 - 그러나 누군가 새 인스턴스가 아닌 기존 인스턴스를 넣는다면?
             *       -> 컴파일 수준에서 잡아줄 방법이 없음
             */

            //member1은 아래 member2와 똑같은 임베디드 타입의 똑같은 값을 가지고 있음

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setHomeAddress(address1);
            em.persist(member2);

            //member1에서 임베디드 타입의 값을 변경하려 시도
            //member1.getHomeAddress().setCity("newCity1"); -> 불변객체로 만들어준 후, 오류가 발생하므로 sideEffect 원천 차단됨
            // -> UPDATE 쿼리는 member1/member2에 대해 2번 나감

            System.out.println("member1.getHomeAddress().getCity() = " + member1.getHomeAddress().getCity());
            System.out.println("member2.getHomeAddress().getCity() = " + member2.getHomeAddress().getCity());
            //member1.getHomeAddress().getCity() = newCity1
            //member2.getHomeAddress().getCity() = newCity1
            // -> sideEffect

            // 값 타입의 공유는, 이렇게 새로운 값타입을 만들고 '복사'를 통해 새로운 인스턴스를 사용해야 한다.
            Address copiedAddress1 = new Address("city2", address1.getStreet(), address1.getZipcode());
            member2.setHomeAddress(copiedAddress1);

            System.out.println("member1.getHomeAddress().getCity() = " + member1.getHomeAddress().getCity());
            System.out.println("member2.getHomeAddress().getCity() = " + member2.getHomeAddress().getCity());
            //member1.getHomeAddress().getCity() = newCity1
            //member2.getHomeAddress().getCity() = city2

            /**
             * 객체 타입(임베디드 타입 포함)의 한계
             * - 항상 값을 복사해서 사용하면, 공유 참조로 인한 sideEffect는 피할 수 있다.
             * - *** '임베디드 타입'처럼, 직접 정의한 '값 타입'은 자바의 '기본 타입'이 아닌, '객체 타입'이다
             *   -> 기본 타입은, 한 쪽의 값을 대입하면 '값'을 '복사'해서 넣는다 -> '공유'자체가 불가능한 구조
             *   -> cf) 객체 타입은 새 인스턴스를 만들어주면 가능하지만, '참조 값'을 직접 대입하는 것을 막을 방법이 없다.
             *   -> 객체의 공유 참조는 피할 수 없다.
             * *** 즉, 신경쓰면 되지만, 실수가 발생할 가능성이 남아있으므로 피할 수 없다.
             *   -> 즉, '컴파일' 단계에서 실수를 방지할 방법이 없다.
             *
             * 해결 - 불변 객체 (immutable object)
             * - '객체 타입'을 '수정'할 수 없게 만들면 -> sideEffect 원천 차단
             *   -> *** '값 타입'은 불변 객체(immutable object)로 설계해야함
             * - 생성 시점 이후, 절대 값을 변경할 수 없는 객체
             * 방법
             * - 생성자로만 '값'을 생성하고 + 1. Setter(수정자)를 만들지 않기 or 2. private으로 변경하기
             *   ex) Integer/String은 JAVA의 대표적 불변객체
             *
             *   *** '불변'이라는 작은 제약으로 'sideEffect'라는 큰 재앙을 막을 수 있다 !!
             */

            /**
             * 그렇다면, 값을 바꾸고 싶은 경우에는 어떻게 해야할까?
             * - 임베디드 타입의 '필드'를 설정하는 것은 막혔으므로
             * -> 임베디드 타입의 '인스턴스' 자체를 새로 생성해서 통으로 바꿔끼우기
             */

            /**
             * 값 타입의 비교 ****************
             * - '인스턴스가 달라도' 그 안의 '값'이 같으면, 같은 것으로 봐야함
             * 동일성(identify) 비교 ***
             * - 인스턴스의 '참조'를 비교 ('참조' 비교가 '값'비교와 동일함)
             * - *** primative 타입 자체는 ConstantPool에 참조로 저장된 공간의 참조에서 '값'자체를 '복사해서' 들고 있고있고,
             *       '해당 참조'를 비교하는 것이 곧 '값'자체를 비교하므로 '값'이 같으면 ==의 연산도 true
             * 동등성(equivalence) 비교 *** ('참조' 비교가 '값'비교와 동일하지 않음)
             * - '인스턴스'의 '참조'가 아닌 내부의 '값'을 비교
             * - by equals()의 overriding
             * - equals()의 default는 "==" 비교와 동일 ***
             * - *** hash 또한 적절하게 손봐줘야함
             *
             * -> '값 타입'은 equals()를 통해 '동등성 비교'*** 를 해야함
             *    + *** Primative/String 타입 중 일부는 '값 타입'이지만 '참조'의 위치가 일반적인 객체생성의 Heap이 아닌 ConstantPool의 symbolic reference이므로 특이한 케이스
             * -> 이에 맞게 equals() 메서드를 '재정의'
             *    + '모든 필드'를 재정의
             */

            int a1 = 10;
            int b1 = 10;
            System.out.println("(a1 == b1) = " + (a1 == b1)); //true

            Address addressA = new Address("city", "street", "1000");
            Address addressB = new Address("city", "street", "1000");

            System.out.println("addressA.hashCode() = " + addressA.hashCode());
            System.out.println("addressB.hashCode() = " + addressB.hashCode());
            System.out.println("addressA.hashCode() == addressB.hashCode() = "
                    + (addressA.getClass().hashCode() == addressB.getClass().hashCode()));

            System.out.println("(addressA == addressB) = " + (addressA == addressB)); //false
            //"=="비교는 reference의 주소 자체를 비교 + hashCode 자체도 다름

            System.out.println("(addressA.equals(addressB)) = " + (addressA.equals(addressB)));
            //1. equals()를 '재정의'하지 않았다면 -> equals()의 default는 "==" 비교와 동일 *** -> false
            //2. equals()를 재정의 했다면 -> true

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }
}
