package jpashop.basicjpql;

import jpashop.basicjpql.samplejpashop.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-basic");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            /**
             * JPQ가 지원하는 다양한 쿼리 방법
             * - DB는 SQL 실행이 필요함
             * - JPQL : 표준 문법. JPA Criteria/QueryDSL 은 JAVA 코드를 통해 JPQL을 빌드해주는 generator 과 같은 것
             * - JPA Criteria
             * - QueryDSL
             * - 네이티브 DSL : 쌩쿼리. JPQL을 사용하더라도, 표준 sql문법에 벗어나는 DB vendor에 종속적인 query가 나갈 수밖에 없을 때 사용
             *   ex) Oracle - connect by
             * - JDBC API 직접 사용 / MyBatis / SpringJdbcTemplate 과 함께 사용
             * *** hibernate 창시자인 게빈 킹
             * - 100프로의 문제를 해결하기 위해 만든 것이 아니다.
             */

            /**
             * JPQL 소개
             * - 기존의 단순한 조회 방법
             *   1. em.find() - EntityManager
             *   2. entityInstance.getA().getB() - '객체 그래프 탐색'
             * - 그런데, 나이가 18살 이상인 회원을 '모두' 검색하고 싶다면?
             *
             * - JPQL은 SQL과 매우 비슷한 문법을 제공
             * - JPA를 사용하면 Entity 객체를 중심으로 개발해야함 + Table 매핑 작업
             * - *** 문제는 '검색 쿼리'
             *   -> 검색을 할 때도, Table이 아닌 Entity 객체를 대상으로 검색해야함
             *   -> 즉, SQL을 짤 때도, Table이 아닌 Entity 객체를 대상으로 작성하는 기능이 필요함
             * - but, 모든 DB Table의 데이터를 '객체'로 변환해서 메모리에 띄우고 검색하는 것은 불가능
             * -> Application이 필요로하는 데이터만 불러오기 위해선 '검색 조건'이 포함된 SQL이 필요
             *    ex) where로 filtering / group by 등..
             * -> 즉, 최소한의 효율적인 데이터만 메모리에 로드하는 것이 효율이자 목표
             *
             * - 이러한 문제 해결을 위해 JPA는 SQL을 '추상화'한 JPQL이라는 '객체 지향 쿼리 언어'를 제공
             * - SQL과 문법 유사
             * - ANSI표준 - SELECT / FROM / WHERE / GROUP BY / HAVING / JOIN 지원
             * - *** JPQL은 'Entity 객체'를 대상으로 쿼리 vs SQL은 DB Table을 대상으로 쿼리
             *   -> *** but, JPQL도 결국 SQL로 번역되어 실행됨
             */

            Member member1 = new Member();
            member1.setUsername("kim");
            em.persist(member1);

            /**
             * JPA가 flush를 호출하는 기준
             * - commit() / *** 'EntityManager'를 통해 Query가 날아갈 때
             */

            em.flush();

            List<Member> resultList1 = em.createQuery( //Member는 Entity를 가리킴 // select 'm'의 m은 Member Entity 자체를 조회해와! 라는 의미
                    "select m From Member as m where m.username like '%kim%'",
                    Member.class
            ).getResultList();

            for (Member member : resultList1) {
                System.out.println("member.getName() = " + member.getUsername());
            }

            /**
             * Hibernate:
             *     / select m From Member m where m.username like '%kim%' /
             *     // *** 실제 이 부분은 주석처리 - 'JPQL' 언어
             *
             *     // 이 아래로는 Entity의 매핑정보를 읽고 alias를 설정해 적절한 'SQL'을 작성
             *     select
             *          member0_.MEMBER_ID as member_i1_6_,
             *          member0_.createBy as createby2_6_,
             *          member0_.createdDate as createdd3_6_,
             *          member0_.lastModifiedBy as lastmodi4_6_,
             *          member0_.lastModifiedDate as lastmodi5_6_,
             *          member0_.city as city6_6_,
             *          member0_.street as street7_6_,
             *          member0_.zipcode as zipcode8_6_,
             *          member0_.username as username9_6_
             *     from
             *          MEMBER member0_
             *     where
             *          member0_.username like '%kim%' */

            /**
             * JPQL
             * - Table이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
             * - SQL을 추상화해서 '특정 DB의 SQL'에 의존 X
             *   -> *** SQL로의 변환 과정에서 특정 DB Dialect 또한 변환해줌
             * -> JPQL을 한마디로 정의하면 - '객체 지향 SQL'
             *
             * 단점
             * - JPQL 자체는 단순한 '문자열' String
             * -> '동적 쿼리'를 만들기 위해서는 String 단위로 잘라내고 if/띄어쓰기 등 까다로움
             *
             * 대안 - JPQL 빌더
             * 1. Criteria - JAVA Persistence 2.0 표준에서 제공하는 문법
             *    - 동적 쿼리 짜는 것이 수월해짐
             *    - Compile 수준에서 오류를 잡을 수 있는 장점도 있음
             *    - but, 가독성이 좋지 않고, 복잡한 쿼리는 너무 복잡해짐 + SQL스럽지가 않음
             *    -> 유지보수가 어려우므로 실무에서 안씀!
             * 2. QueryDSL
             *    - 실무 사용 권장
             *    - JPQL과 거의 흡사 + Documents 잘되어있어서 JPQL만 잘하면 됨
             * 3. 네이티브 SQL
             *    - JPA가 제공하는 SQL을 직접 사용하는 기능
             *    - JPQL로 해결할 수 없는 '특정 DB에 의존적인 기술'을 가능하게 함
             *    ex) Oracle - connect by / 특정 DB만 사용하는 SQL 힌트
             * 4. JDBC 직접사용 + * SpringJdbcTemplate(강사님은 네이티브 SQL대신 사용)
             *    - JPA를 사용하면서 JDBC 커넥션 직접사용 + SJT + MyBatis
             *    - * 단, 영속성 컨텍스트를 적절한 시점에 강제로 flush하는 것이 필요
             *    ex) JPA를 '우회해서' SQL을 실행하기 직전에, 영속성 컨텍스트 '수동 flush'
             *    ex) em.persist()는 em.commit() 시점에 SQL 실행
             */

            //1. Criteria 사용 준비
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> query1 = cb.createQuery(Member.class);

            //루트 클래스 ( 조회를 시작할 클래스 )
            Root<Member> m1 = query1.from(Member.class);

            CriteriaQuery<Member> criteriaQuery1 = query1.select(m1);
            //if 조건도 사용 + 동적 구성 가능
            String username1 = "username";
            if (username1 != null) {
                criteriaQuery1 = criteriaQuery1.where(cb.equal(m1.get("username"), "kim"));
            }

            List<Member> resultList2 = em.createQuery(criteriaQuery1)
                    .getResultList();

            for (Member member : resultList2) {
                System.out.println("member.getUsername() = " + member.getUsername());
            }
            //Hibernate:
            //    /* select
            //        generatedAlias0
            //    from
            //        Member as generatedAlias0
            //    where
            //        generatedAlias0.username=:param0 */ select
            //            member0_.MEMBER_ID as member_i1_6_,
            //            member0_.createBy as createby2_6_,
            //            member0_.createdDate as createdd3_6_,
            //            member0_.lastModifiedBy as lastmodi4_6_,
            //            member0_.lastModifiedDate as lastmodi5_6_,
            //            member0_.city as city6_6_,
            //            member0_.street as street7_6_,
            //            member0_.zipcode as zipcode8_6_,
            //            member0_.username as username9_6_
            //        from
            //            MEMBER member0_
            //        where
            //            member0_.username=?
            //여튼 쉬운 것 같아보여도 복잡하고 어려움

            //3. 네이티브 SQL


            List<Member> resultList3 = em.createNativeQuery("select MEMBER_ID, city, street, zipcode, username from MEMBER", Member.class)
                    .getResultList();

            for (Member member : resultList3) {
                System.out.println("member.getUsername() = " + member.getUsername());
            }
            
            //Hibernate:
            //    /* dynamic native SQL query */ select
            //        MEMBER_ID,
            //        city,
            //        street,
            //        zipcode,
            //        username
            //    from
            //        MEMBER

            /**
             * Flush와 SQL 관련 기술들의 문제점
             * - EntityManager를 통해 Query를 수행하므로 JPA 내부적으로 flush 호출됨
             * - but, JDBC를 통해 직접 connection을 획득해서 query를 날린다고 가정
             *   ex) dbconn.executeQuery("select * from Member")
             *   -> JPA와 관련 없음 + persist()한 entity는 영속성 컨텍스트에서 flush되지 않은 상태
             *   -> SELECT 쿼리를 뿌려봐야 데이터를 가져올 수 없음
             *
             * 해결
             * - 강제로 flush() 호출
             */

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
