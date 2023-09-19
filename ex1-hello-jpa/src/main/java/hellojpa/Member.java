package hellojpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
// - 내부적으로 JPA가 인식하는 이름
// ** cf) table : 실제 DB에서 사용할 이름 설정 가능
// - name : default는 현재 클래스와 동일한 이름
// ** 다른 package에 같은 이름의 entity가 존재할 경우, 다른 entity는 다른 이름을 사용해야 함
@Entity(name = "MEMBER2")

//@Table
// - 실제 DB에서 사용할 table의 이름 설정 가능
// ** DDL(Data Definition Language) - 실제 JPA는 '어플리케이션 로딩' 시점에 미리 table을 생성하는 '데이터베이스 스키마 자동 생성' 기능이 있음 - *** 실제 운영이 아닌 개발 단계에서만 사용
//    ex) CREATE/ALTER/DROP/TRUNCATE
//    + DB dialect에 따라 적절한 DDL 생성
//    + *** 새롭게 추가되는 필드도 테이블 생성 시점에 모두 인식 후 테이블을 생성
//    + *** cf)DDL 생성기능 - name 속성의 경우 INSERT/UPDATE... 쿼리로 JPA 자체의 runtime 로직에 영향을 주지만, @Column의 unique같은 경우 DDL 생성에만 관여
// <property name="hibernate.hbm2ddl.auto" value="create" />
// - create : DROP + CREATE
// - create-drop : create 실행 후 어플리케이션 종료 시점에 DROP 추가 *** 테스트 케이스 작성에 용이
// - update : ALTER table + add column을 통해 변경된 부분만 적용 *** 지우는 것은 변경사항 적용되지 않음
// - validate : Entity와 Table의 정상매핑을 확인만 함
// - none : 주석처리도 귀찮을 때 관리상 none이라고 작성. 아무 다른 문자 적어도 상관없음.
@Table(name = "MEMBER3")
public class Member {

    public Member() {}

    public Member(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    private Long id;
    private String name;

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
}
