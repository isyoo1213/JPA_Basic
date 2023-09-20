package hellojpa;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Entity
public class SampleMember {

    @Id
    private Long id;

    //@Column의 소것ㅇ
    // 1. name - 필드와 매핑할 테이블의 컬럼 이름
    // 2. insertable/updatable - DB에 INSERT or ALTER 가능 여부 - default : true
    // 3. nullable (DDL) - false인 경우, DDL에 not null 조건이 걸림
    // 4. unique (DDL) - unique key를 설정하는 방식 but, 쿼리에서의 column 이름이 변하므로 사용하지 않음 -> Entity 객체의 @Table(uniqueConstraints='column')으로 unique key 설정 추천
    // 5. columnDefinition (DDL) - 컬럼 정보를 '직접' 줄 수 있고 DDL에 반영됨
    //    ex) columnDefinition = "varchar(100) default 'EMPTY'"
    //    cf) length (DDL) - 설정한 *String 타입의 byte/글자수에 따라 varchar(byte or 글자수)로 설정 (DB의 length()에 따라 다르다고 함)
    // 6. precision/scale (DDL) - precision은 소수점을 포함한 전체 자리수 / scale은 소수점 자리수
    //    *** BigDecimal / BigInteger 에만 사용 가능 + double/float에는 사용 불가능

    @Column(name = "name") //객체와 DB테이블의 컬럼을 다르게 설정하는 경우 -> DB는 name / 객체는 username
    private String username;

    private Integer age; //JAVA 기준의 타입을 사용해도, JPA가 가장 비슷한 유형으로 생성해줌

    private BigDecimal bigDecimalAmount;
    private BigInteger bigIntegerAmount;

    // *** 실제 DB에는 enum 타입이 존재하지 않음
    //@Enumerated를 통해 처리하도록 가능
    // *** EnumType.ORDINAL - enum '순서'를 DB에 저장 - default + Integer 타입으로 컬럼을 생성함 -> '숫자'로 데이터 저장됨 + enum의 순서가 바뀐채 추가되면 숫자의 의미가 사라짐
    // *** EnumType.STRING - enum '이름'을 DB에 저장 - 무조건 STRING 사용
    @Enumerated(EnumType.STRING) //javax.persistence
    private RoleType roleType;

    // *** 날짜/시간에 관한 타입
    // @Temporal - enumType으로 1. DATE 2. TIME 3. TIMESTAMP가 존재
    // 실제로 JAVA에는 날짜/시간에 관한 타입이 세분화되어 있지만 DB는 주로 위의 3가지로 구분해서 사용
    // *** JAVA 8 이후로 hibernate가 LocalDate/LocalDateTime을 지원하면서 생략가능해짐
    //     LocalDate - date 타입 / LocalDateTime - timestamp 타입
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    // @Lob
    // varchar를 뛰어넘는 내용을 위한 유형
    // 문자타입(String/char[]/java.sql.CLOB)일 경우, clob 유형으로 생성
    // ex) description clob,
    // 다른 타입일 경우 blob 유형으로 생성 (byte[], java.sql.BLOB)
    @Lob
    private String description;

    // ** @Transient
    // 객체에는 존재히만, DB에는 연동시키고 싶지 않은 데이터를 활용하고 싶은 경우
    // 메모리에만 임시로 저장하고 싶은 경우
    @Transient
    private int temp;

    /** create 옵션일 경우 스키마 자동 생성 DDL
     * Hibernate:
     *
     *     create table SampleMember (
     *        id bigint not null,
     *         age integer,
     *         createDate timestamp,
     *         description clob,
     *         lastModifiedDate timestamp,
     *         roleType varchar(255),
     *         name varchar(255),
     *         primary key (id)
     *     )
     */
}
