package jpashop.jpainheritance;

import jpashop.jpainheritance.inheritancemapping.Album;
import jpashop.jpainheritance.inheritancemapping.ItemH;
import jpashop.jpainheritance.inheritancemapping.Movie;
import jpashop.jpainheritance.mappedsuperclass.MemberM;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {

            /**
             * 상속관계 매핑
             * - * 관계형 데이터베이스는 상속관계 X
             * - 슈퍼/서브 타입 관계라는 '논리적''모델링 기법'이 '객체 상속'과 유사
             * - 상속관계 매핑 : 객체의 상속 구조와 DB의 슈퍼/서브타입 관계를 매핑
             * -> 즉, DB의 '논리적 모델링'을 객체의 '상속 구조'를 통해 3가지 '물리적 방식'으로 구현해내는 것
             * 1. JoinStrategy
             * - 자식 테이블은 부모 테이블의 PK를 PK/FK로 동시에 사용함
             */

            Movie movie1 = new Movie();
            movie1.setDirector("diroector1");
            movie1.setActor("actor1");
            movie1.setName("movieName1");
            movie1.setPrice(10000);

            em.persist(movie1);

            //영속성 컨텍스트 flush/비우기
            em.flush();
            em.clear();

            Movie findMovie1 = em.find(Movie.class, movie1.getId());
            /* 실제 쿼리
            Hibernate:
                select
                    movie0_.ITEMH_ID as itemh_id1_2_0_,
                    movie0_1_.name as name2_2_0_,
                    movie0_1_.price as price3_2_0_,
                    movie0_.actor as actor1_3_0_,
                    movie0_.director as director2_3_0_
                from
                    Movie movie0_
                inner join // -> inner 조인을 통해 연결된 테이블의 정보를 가져옴
                    ITEMH movie0_1_
                        on movie0_.ITEMH_ID=movie0_1_.ITEMH_ID
                where
                    movie0_.ITEMH_ID=?
             */
            System.out.println("findMovie1 = " + findMovie1);
            /**
             * 즉, 상속 매핑의 경우에도 JPA는 INSERT가 2번 필요할 경우든, 조회시 JOIN이 필요한 경우 모두 처리해줌
             */

            /**
             * 상속관계 매핑
             * 2. SingleTable
             * - 단순하게 부모 table에 모든 column을 생성하고 + ** DTYPE, 즉 자식 Entity의 유형 column까지 자동으로 생성
             *   *** @DiscriminatorColumn을 추가하지 않아도, 자동으로 생성
             *   + 자식 Entity에서는 @DiscriminatorValue를 통해 '값' 변경 가능
             * - INSERT 쿼리가 1번만 들어가므로 성능 이점
             * - SELECT 쿼리 또한 JOIN이 필요 없으므로 성능 이점
             *
             * *** 상속매핑에있어 JPA의 장점
             * @Inheritance(strategy = InheritanceType.xxx)를 통해 쿼리 수정 없이 적은 비용으로 변경이 용이함
             */

            /**
             * 상속관계 매핑
             * 3. TABLE_PER_CLASS
             * *** 부모 entity는 table로 구성하지 않음 -> 부모 entity를 *** 'abstract 클래스로 구성'하면 테이블로 생성하지 않음
             * 부모 테이블의 PK는 자식 테이블들이 시퀀스처럼 공유하며 PK로 사용
             * 부모 테이블의 column은 각각 자식 테이블에 '중복되더라도' column으로 생성
             */

            Album album1 = new Album();
            album1.setArtist("artist1");
            album1.setName("albumName1");
            album1.setPrice(10000);

            em.persist(album1);

            /**
             * TABLE_PER_CLASS 의 문제점
             * 상황 : 영속성 컨텍스트가 비어있을 때 + 부모 Entity(ItemH)의 Id만으로 조회를 해야하는 경우
             * -> 모든 자식 entity의 Table을 돌며 해당 Id를 조회해야함 (by union)
             *
             * 즉, 명확하게 조회할 Entity의 Table을 알고 있을 경우 문제되지 않지만,
             * 다형성을 사용해야 하는 경우, 즉 부모 Entity로 접근해야 한다면 비효율적인 성능 단점
             */

            em.flush();
            em.clear();

            ItemH findItem2 = em.find(ItemH.class, album1.getId());
            System.out.println("findItem2.getName() = " + findItem2.getName());

            /**
             * 상송관계 전략 정리
             * 1. Join Strategy
             * 장점
             * - 테이블 정규화
             * - 외래 키 참조 무결성 제약조건 활용 가능 (ITEMH_ID가 자식 테이블의 FK로 사용될 때 null이 될 수 없음)
             *   + 상속관계 외부에 있는 Table에서 부모 Table 조회시, 부모 Table의 데이터로 충분한 경우 자식 테이블 조회가 필요없다.
             * - 저장공관 효율화 by 정규화
             * 단점
             * - 조회 시 JOIN을 많이 사용 -> 성능 저하 - 그렇게 문제가 아님
             * - 조회 쿼리가 복잡함
             * - Data 저장 시, INSERT 쿼리 2번 호출
             * -> but, 성능이 그렇게 단점이 되는 부분은 아니다 + JOIN 최적화로 개선 / 저장공간 효율화로 인한 성능이점이 상쇄될 수도 있다. -> 정석!
             *
             * 2. SINGLE TABLE Strategy
             * 장점
             * - JOIN이 필요 없으므로 일반적으로 조회 성능이 빠름
             * - 조회 쿼리가 단순함
             * 단점
             * - *** 자식 Entity가 매핑한 Column은 모두 null 허용 -> 치명적! 데이터 무결성 입장에선 단점
             * - 단일 테이블에 모든 것을 저장하므로, 테이블이 커질 수 있고, 상황에 따라 조회 성능이 오히려 느려질 수 있다
             * -> but, 해당 임계점을 넘는 것이 쉽진 않으므로 일반적으로 성능이 더 좋다
             *
             * 3. TABLE_PER_CLASS strategy
             * - *** 결론적으로는 쓰면 안되는 전략. DBA/ORM전문가 둘다 추천 X
             *       ex) price를 통해 정산을 해야한다
             *       join/singleTable은 모두 단독 테이블에서 살펴볼 수 있다.
             *       but, 이 전략은 모든 테이블에서 정산을 돌려야하고, 테이블이 추가되거나 변경될 때 작업해주어야함
             * 장점
             * - 서브타입(자식 Entity)을 명확하게 구분해서 처리할 때 효과적 - INSERT/SELECT
             * - Table마다 column에 not null 전략 사용 가능
             *단점
             * - 여러 자식 테이블을 함꼐 조회할 때 성능이 느림(UNION SQL) ex) 다형성을 통한 부모 Entity의 정보로 조회
             * - 자식 테이블을 통합해서 쿼리하기 어려움
             * -> 시스템의 '변경'의 관점에서 매우 불리
             */

            /**
             * MappedSuperclass - 매핑정보 상속
             * 상속관계가 아닌 테이블 사이에서, 공통적으로 사용해야하는 속성, 매핑 정보가 필요한 경우
             *    ex) createdDate, lastModifiedBy... 등등의 메타정보
             * 부모 클래스에 해당 속성들을 필드로 생성한 후, @MappedSuperclass 어노테이션만 붙여주면, 각각의 Table에 column이 생성됨
             * - 필드에서는 실제 추가적인 속성 정보들을 자동화로 추가함
             * - immutable한 테이블이 아니면, 이러한 메타정보는 매우 필수적임
             * - *** 상속관계 매핑 X + Entity X + Table과 매핑 X -> *** 실제 Table이 생성되지 않음
             * - 조회/검색 불가 ( em.find(BaseEntity) 불가)
             *   cf) 상속관계 매핑의 경우 - em.find(ItemH.class, album1.getId())로 다형성 조회 가능함
             * - 부모클래스를 상속받는 '자식 클래스 Entity'에만 매핑 정보 제공
             * - *** 직접 생성해서 사용하는 경우는 없으므로, '추상 클래스' 권장
             *
             * *** Entity 클래스가 상속 가능한 클래스
             * 1. 다른 @Entity 클래스
             * 2. @MappedSuperclass로 지정한 클래스
             */

            MemberM member1 = new MemberM();
            member1.setUsername("User1");
            member1.setCreatedBy("Yoo");
            member1.setCreatedDate(LocalDateTime.now());

            em.persist(member1);
            /* Hibernate:
                create table MEMBERM (
                   MEMBERM_ID bigint not null,
                    createdBy varchar(255),
                    createdDate timestamp,
                    lastModifiedBy varchar(255),
                    lastModifiedDate timestamp,
                    username varchar(255),
                    TEAMM_ID bigint,
                    primary key (MEMBERM_ID) ) */

            em.flush();
            em.clear();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
