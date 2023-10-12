package jpashop.jpqlgrammar;

import jpashop.jpqlgrammar.sample.*;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-basic");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            /**
             * JPQL 문법
             * select_문 :: =
             *      select_절
             *      from_절
             *      [where_절]
             *      [groupby_절]
             *      [having_절]
             *      [orderby_절]
             * upadate_문 :: = update_절 [where_절]
             * delete_문 :: = delete_절 [where_절]
             *
             * *** 벌크 연산 - 단 건 단 건이 아닌 몇백만 건의 변경도 쿼리 한 번으로 수행하는 기능
             *     ex) 전 사원의 연봉을 10% 인상시키기
             * - JPA에서는 update를 따로하지 않고, 값의 변경이 tx.commit() 시점에 자동으로 쿼리가 날아감
             *   -> 이 것은 한 건 한 건이 쿼리로 만들어짐
             * - 벌크 연산은 쿼리 하나로 모든 건을 다루는 기술
             *
             * 문법 예시
             * - select m from Member as m where m.age > 18
             * - 'Entity(Member)'와 '속성(age)'은 대소문자 구분함
             * - JPQL 키워드는 대소문자 구분 X ex) SELECT / from / where
             * - 'Table' 이름이 아닌 'Entity' 이름 사용 (Member)
             *    ex) @Entity(name = "MM") 으로 지정했다면, Entity 이름은 MM으로 사용해야함
             *        + default는 Entity 클래스의 이름 (Member)
             * - *** 별칭은 필수 (ex) Member as m) + as는 생략 가능
             *
             * 집합과 정렬
             * - ANSI SQL의 표준 function 적용 가능
             * ex) select COUNT(m), SUM(m.age), AVG(m.age), MAX(m.age), MIN(m.age) from Member m
             * - GROUP BY / ORDER BY 모두 같은 방식으로 사용 가능
             *
             * TypeQuery / Query
             * - TypeQuery : 반환 타입이 명확할 때 사용
             * - Query : 반환 타입이 명확하지 않을 때 사용
             *
             * 1. Type 정보가 명확한 경우
             * TypedQuery<Member> query1 = em.createQuery("select m from Member m ", Member.class);
             * TypedQuery<String> query2 = em.createQuery("select m.username from Member m ", String.class);
             * - 2nd Arg(Member.class / String.class)는 Type 정보
             * - Type 정보는 기본적으로 Entity가 가능함
             * - Type 정보를 명시한 경우 return 타입을 받으면 -> TypeQuery<Member/String> 로 받게됨
             *   -> *** 즉, Generic을 가지고 있음
             *
             * 2. Type 정보가 명확하지 않은 경우
             * Query query3 = em.createQuery("select m.username, m.age from Member m ");
             * - TypeQuery<Generic>이 아닌, Query 형으로 받게 됨
             *
             * 결과조회 API
             * - 결과가 Collection일 경우 - List<Generic> resultList = query.getResultList();
             *   + query는 TypeQuery<Generic> 형
             *   + 결과가 없으면 '빈 List' 반환
             *   -> *** NullPointerException 걱정 안해도 됨!!
             * - 결과가 '정확하게 단 하나' - Member singleResult = query.getSingleResult();
             *   1. 결과가 없으면 - java.persistence.NoResultException
             *      -> but, 없으면 Exception이 아닌, 그냥 없는 결과를 얻고 싶을지도?
             *      -> *** Spring Data JPA를 사용하게되면, null/Optional로 반환하는 추상화된 방법 가능
             *             + getSingleResult()는 표준 스펙이므로 어쩔수 없이 호출
             *             + 호출 후 try/catch로 Exception을 처리해주는 방식
             *   2. 결과가 둘 이상이면 - java.persistence.NonUniqueResultException
             */

            Member member1 = new Member();
            member1.setUsername("kim");
            member1.setAge(10);

            em.persist(member1);

            //1. Type 정보가 명확한 경우
            TypedQuery<Member> query1 = em.createQuery("select m from Member m ", Member.class);
            TypedQuery<String> query2 = em.createQuery("select m.username from Member m ", String.class);
            TypedQuery<Integer> query3 = em.createQuery("select m.age from Member m where m.username = 'kim'", Integer.class);
            TypedQuery<Member> query4 = em.createQuery("select m from Member m where m.username = 'kim'", Member.class);
            //2nd Arg(Member.class / String.class)는 Type 정보
            //Type 정보는 기본적으로 Entity가 가능함
            //Type 정보를 명시한 경우 return 타입을 받으면 -> TypeQuery<Member/String> 로 받게됨
            //즉, *** Generic을 가지고 있음

            List<Member> resultList1 = query1.getResultList();
            for (Member member : resultList1) {
                System.out.println("member.getUsername() = " + member.getUsername());
            }

            List<String> resultList2 = query2.getResultList();
            for (String s : resultList2) {
                System.out.println("s = " + s);
            }

            Integer singleResult1 = query3.getSingleResult();
            System.out.println("singleResult1 = " + singleResult1);

            Member singleResult2 = query4.getSingleResult();
            System.out.println("singleResult2.getUsername() = " + singleResult2.getUsername());

            //2. Type 정보가 명확하지 않은 경우
            Query query5 = em.createQuery("select m.username, m.age from Member m ");
            // TypeQuery<Generic>이 아닌, Query 형으로 받게 됨

            List resultList3 = query5.getResultList();
            for (Object o : resultList3) {
                System.out.println("o = " + o);
            }

            /**
             * Parameter 바인딩
             * 1. '이름' 기준
             * - =:parameter + query.setParameter("parameter", "value");
             * 2. '위치' 기준
             * - 사용하지 말 것
             * *** 이름 기준은 '위치'가 변해도 오류를 피할 수 있으므로 이름 기준의 parameter 바인딩을 사용할 것
             */

            //실사용은 '메서드 체인'을 활용해서 곧바로 result를 얻게끔 작성
            Member singleResult3 = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "kim")
                    .getSingleResult();
            System.out.println("singleResult3.getUsername() = " + singleResult3.getUsername());

            /**
             * 프로젝션
             * - SELECT 절에 조회할 대상을 지정하는 것
             * - 프로젝션 대상 : Entity / Embedded Type / Scalar Type(숫자, 문자 등 기본 데이터 타입)
             * 예시
             * SELECT m from Member m  -  Entity 프로젝션 (m은 Entity의 alias)
             * SELECT m.team from Member m  -  Entity 프로젝션
             * SELECT m.address from Member m  -  Embedded Type 프로젝션
             * SELECT m.username, m.age from Member m  -  Scalar타입
             * - DISTINCT로 중복제거 가능
             */

            /**
             * 그렇다면, JPQL을 통해 'Entity 프로젝션'을 사용해 생성한 쿼리로 받아온 결과는 영속성 컨텍스트에 저장이 될까?
             * - *** 우선 JPQL 내의 'm'은 단순한 'Entity'
             * - ex) List<Member> resultList = .... .getResultList();
             *   -> List의 Generic인 <Member>는 실제 데이터를 담고 있는 Entity이자 영속성 컨텍스트에 저장된 것일까?
             *   -> Yes
             * -> ***** Entity 프로젝션의 JPQL 쿼리에 걸려있는 Entity는 모두 영속성 컨텍스트에서 관리해버림
             * -> Entity의 정보 변경시 모두 반영됨
             */
            em.flush();
            em.clear();

            //Entity 프로젝션의 영속성 컨텍스트 확인 예시
            List<Member> resultList4 = em.createQuery("select m from Member m", Member.class)
                    .getResultList();
            Member findMember1 = resultList4.get(0);
            findMember1.setAge(20);

            /**
             * DB에서 다른 테이블을 참조해야하는 경우
             * - JPQL이 작성한 쿼리는 join을 사용한 쿼리로 변환
             * - but, *** join은 튜닝/성능에 대한 이슈 + join이 사용된다는 가시성 (* 명시적 join)
             *   -> JPQL의 간단한 표현보단 SQL과 근접하게 작성하는 것이 바람직함
             */

            //다른 테이블을 참조해야 하는 경우 -> 현재는 inner join으로 쿼리 나가는 것을 확인 가능
            List<Team> resultList5 = em.createQuery("select m.team from Member m", Team.class)
                    .getResultList();
            //        inner join
            //            Team team1_
            //                on member0_.TEAM_ID=team1_.TEAM_ID

            //SQL과 근접한 방식으로 다시 작성한 JPQL 쿼리
            List<Team> resultList6 = em.createQuery("select t from Member m join m.team t", Team.class)
                    .getResultList();


            /**
             * Embedded Type 프로젝션의 한계 - *** '값 타입'의 한계
             * - Embedded Type은 Entity에 '소속'되므로, 단독으로 사용할 수 없음
             * - Entity의 필드를 사용하는 것처럼, Entity.EmbeddedType으로 접근해야함
             */
            //Embedded Type 프로젝션
            Order order1 = new Order();
            order1.setAddress(new Address("city1", "street1", "00001"));

            em.persist(order1);

            List<Address> resultList7 = em.createQuery("select o.address from Order o", Address.class)
                    .getResultList();
            for (Address address : resultList7) {
                System.out.println("address.getCity() = " + address.getCity());
            }


            /**
             * Scalar Type 프로젝션
             * - JPQL에 반환 Type을 명시하지 않음
             *
             * 1. Query Type으로 조회 : Generic없이 List로만 받는 경우
             * - em.createQuery("JPQL")의 반환형은 'Query'
             *   - getResultList() - List 반환
             *      -> Object o = resultList.get(0) - List이므로 index로 원소 접근 가능
             *          + *** 각각 타입을 담은 결과 1개가 Object 1개를 구성
             *          -> 각각의 타입은 1개의 Object 내에 들어가있을 것으로 예상
             *          -> Object[] object = (Object[]) o - 각각의 타입을 답은 오브젝트를 배열로 캐스팅
             *   - getSingleResult() - Object 반환
             *     -> 위의 과정에서 Object를 처리하는 과정과 동일
             *
             * 2. Object[] Type으로 조회 : Generic을 통해 List<Object[]>로 받는 경우
             * - *** unchecked 경고가 뜸
             * - Object -> Object[]로 캐스팅하는 과정까지 생략할 수 있음
             * - but, List<Object[]>에서 원소를 가져오는 과정은 거쳐야함
             *
             * 3. new 명령어로 조회 : 단순 값을 DTO로 바로 조회 - 표준 맞음
             * - select 'new' 'java 아래의 package 경로'.'DTO클래스 이름'(entityAS.field1, entityAS.field2) ..., DTO.class
             * - package 이름을 포함한 DTO 클래스 명을 모두 입력해야함
             * - *** '순서'와 'Type'이 일치하는 '생성자'필요
             *
             */
            //Scalar Type 프로젝션
            //1. Query Type으로 조회 : Generic없이 List로만 받는 경우
            Query query8 = em.createQuery("select distinct m.username, m.age from Member m");
            List resultList8 = query8.getResultList();
            Object o = resultList8.get(0);
            Object[] objects = (Object[]) o;

            /**
             * 궁금증
             * Object를 오브젝트 배열로 변환하는 과정에서, 어떤 자료형들이 담겨있는지 확인할 수 있는 방법은 없을까?
             */
            //for (Class<?> aClass : resultList8.getClass().getClasses()) {
            //    System.out.println("aClass.getName() = " + aClass.getName());
            //}

            System.out.println("objects[0] = " + objects[0]);
            System.out.println("objects[1] = " + objects[1]);

            //2. Object[] Type으로 조회 : Generic을 통해 List<Object[]>로 받는 경우
            @SuppressWarnings("unchecked")
            List<Object[]> resultList9 = em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList();
            Object[] objects2 = resultList9.get(0);
            System.out.println("objects2[0] = " + objects2[0]);
            System.out.println("objects2[1] = " + objects2[1]);
            
            //3. new 명령어로 조회
            List<MemberDTO> resultList10 = em.createQuery("select distinct new jpashop.jpqlgrammar.sample.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();

            MemberDTO findMemberDTO1 = resultList10.get(0);
            System.out.println("findMemberDTO1.getUsername() = " + findMemberDTO1.getUsername());
            System.out.println("findMemberDTO1.getAge() = " + findMemberDTO1.getAge());

            /**
             * 페이징 API
             * - JPA는 페이징을 두 API로 추상화함
             * 1. setFirstResult(int startPosition)
             * - 조회 시작위치 ( 0부터 시작 )
             * 2. setMaxResults(int maxResult)
             * - 조회할 데이터 수
             *
             * * 테스트할 경우, 쿼리에 order by를 적용해 sorting이 가능한지 확인
             *
             * *** Dialect에 대한 유연한 변경
             * - H2Dialect -> OracleDialect로 변경하더라도 그에 맞게끔 실제 작성하는 sql 변경됨
             *     ex) order by : H2 - limit/offset, Oracle - rowNum 3depth select
             *   + 실제로 Dialect마다 사용되는 쿼리의 sorting 인덱스의 시작이 0, 1로 다를 수있지만,
             *     이 또한 setFIrstResult()/setMaxResults가 모두 알아서 적용해줌
             * - but, JPA가 변환해주는 dialect도 버전에 따라 레거시들은 오류가 발생하기도 함
             * - 즉, JPA는 표준 스펙을 제공 -> JPQL은 이 스펙을 따르는 언어기능, JPA는 Dialect에 따른 변환기능
             * - SpringDataJPA 또한 JPA 표준에 맞춰 '추상화된' paging 기능을 제공
             */

            for (int i = 0; i < 100; i++) {
                Member member = new Member();
                member.setUsername("membmer" + i);
                member.setAge(i);
                em.persist(member);
            }

            em.flush();
            em.clear();

            //order by까지 적용해봐야 sorting이 잘 적용되는지 확인할 수 있음
            List<Member> resultList11 = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();

            System.out.println("resultList11.size() = " + resultList11.size());
            for (Member member : resultList11) {
                System.out.println("member = " + member);
            }

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
