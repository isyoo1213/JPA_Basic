package jpashop.proxy;

import jpashop.proxy.domain.Member;
import jpashop.proxy.domain.TeamP;
import org.hibernate.Hibernate;

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
             * MEMBER를 조회할 때 TEAM도 조회해야 할까? - 현재 '다대일' '양방향' + 주인은 MEMBER
             * 즉, 1. member를 조회할 때 team은 제외하고 가져오고 싶거나 / 2. member와 team을 동시에 가져오고 싶거나
             * -> JPA는 'Proxy'/'LazyLoading'으로 이를 해결함
             */

            Member member1 = new Member();
            member1.setUsername("member1");

            TeamP team1 = new TeamP();
            team1.setName("team1");

            member1.changeTeam(team1);

            em.persist(member1);
            em.persist(team1);

            em.flush();
            em.clear();

            Member findMember1 = em.find(Member.class, member1.getId());
            //case1. member와 team을 같이 출력하는 비즈니스 로직이 있다고 가정
            printMemberAndTeam(findMember1);

            //case2. member만 출력하는 비즈니스 로직이 있다고 가정
            printMember(findMember1);

            /**
             * Proxy 기초
             * * em.find() vs em.getReference()
             * - 1. em.find() : DB를 통해서 실제 Entity 객체 조회
             * - 2. em.getReference() : DB 조회를 '미루는' '가짜(프록시)' Entity 객체 조회
             *     -> DB에 쿼리가 날아가지 않는데, 객체가 조회됨
             */

            em.flush();
            em.clear();

            Member member2 = new Member();
            member2.setUsername("member2");

            em.persist(member2);

            em.flush();
            em.clear();
            //영속성 컨텍스트가 비워진 상태

            //1. em.find()
            //Member findMember2 = em.find(Member.class, member2.getId());

            //2. em.getReference()
            Member findMember2 = em.getReference(Member.class, member2.getId());

            // *** 단순히 em.getReference()를 실행할 때는 SELECT 쿼리가 나가지 않음
            //     + Id값은 이미 member2.getId()에 값이 있으므로 DB통하지 않아도가져올 수 있음
            // -> 가짜인 Proxy 객체가 조회된 것
            System.out.println("before - findMember2.getId() = " + findMember2.getId());

            // but, 아래 출력처럼 em.getReference()를 통해 가져온 값을 실제 사용하는 시점에는 DB에 쿼리를 날림
            // -> ** 영속성 컨텍스트에 '초기화 요청'을 통해 DB에서 가져온 데이터로 target에 실제 객체를 구성해 반환된 것을 사용
            System.out.println("findMember2.getUsername() = " + findMember2.getUsername());

            // 2번쨰 호출은 target에 값이 있으므로, SELECT 쿼리가 날아가지 않음
            // + *** Proxy 객체 자체가 '실제 엔터티'로 바뀌는 것이 아닌, Proxy 그 자체로 존재
            // -> 오로지 Proxy객체를 '통해' 실제 엔터티로 '접근 가능'하게 되는 것
            System.out.println("after - findMember2.getUsername() = " + findMember2.getUsername());

            //그렇다면 em.getReference()를 통해 가져온 데이터의 정체는 뭘까?
            System.out.println("findMember2 = " + findMember2);
            System.out.println("findMember2 = " + findMember2.getClass());
            //findMember2 = jpashop.proxy.domain.Member@60e21209
            //findMember2 = class jpashop.proxy.domain. *** 'Member$HibernateProxy$ltGGE570' -> hibernate가 만든 '가짜 클래스' = Proxy 클래스

            /**
             * hibernate가 만든 '가짜 클래스', Proxy 클래스의 정체
             * - 껍데기는 같지만, 안이 텅텅 빈 클래스 + Member target(Id값만 들고 있음?) by hibernate 내부 라이브러리
             * - 내부의 target이 실제 reference참조를 보관하고 가리킴
             * - getName()이 호출되면, 실제 target의 참조에 있는 실제 클래스의 getName()을 홏룰함
             * 정체
             * - 실제 Entity를 상속받아서 만들어짐 -> 겉모양이 같음
             * - 이론상 proxy/실제 객체를 구분해서 사용하지 않아도 됨 by 다형성 but, 유의점 존재
             */

            /**
             * 프록시 객체의 초기화
             * 1. 최초 em.getReference()로 가져온 객체는 Proxy 객체
             *    -> target에 값이 없는 상태
             * 2. Proxy 객체에서 실제 getName()이 호출되면 target이 비어있음
             * 3. JPA는 ** 영속성 컨텍스트에 실제 객체의 ** 초기화를 요청
             * 4. 영속성 컨텍스트는 DB를 조회해 데이터를 받아 실제 객체를 생성
             * 5. Proxy 객체의 Member target과 실제 객체를 연결
             * -> 이후에는 target에 걸린 실제 객체의 참조로 DB 조회가 필요없어짐
             */

            /**
             * 프록시의 특징
             * - 프록시 객체는 처음 사용할 때 '한번만 초기화' (target에 값을 넣을 때만 초기화)
             * - 프록시 객체를 초기화할 때, 프록시 객체가 ** '실제 엔터티'로 바뀌는 것은 아님
             *   -> 초기화되면, 프록시 객체를 '통해' 실제 엔터티에 접근가능한 것. 교체 X
             *   -> ***영속성 컨텍스트에 실제가 저장되면 Proxy가 아닌 실제를 가져오므로 getReference()를 호출하더라도 Proxy가 아닌 실제 객체임!!!
             * - 프록시 객체는 '원본 엔터티'를 '상속'받음
             *   -> 타입이 서로 다르므로, 타입 체크시 '=='비교가 아닌 'instance of' 비교를 사용해야함
             * - 영속성 컨텍스트에 이미 entity가 저장되어있다면, em.getReference()를 호출해도 실제 entity가 반환될 수 있음
             * -
             */

            em.flush();
            em.clear();

            //class 타입만 비교해보기
            //1. em.find()
            Member findMember3 = em.find(Member.class, member1.getId());
            Member findMember4 = em.find(Member.class, member2.getId());
            
            System.out.println("findMember3 == findMember4 : " + (findMember3.getClass() == findMember4.getClass())); //true

            em.flush();
            em.clear();

            //2. em.getReference()
            Member findMember5 = em.find(Member.class, member1.getId());
            Member findMember6 = em.getReference(Member.class, member2.getId());

            System.out.println("findMember5 == findMember6 : " + (findMember5.getClass() == findMember6.getClass())); //false
            /**
             * 실제 실무에서는 이렇게 메서드로 뽑아내서 m1, m2 파라미터를 받기만 함
             * -> 실제로 넘어오는 인스턴스가 proxy인지 실제 객체인지 알 수 없음
             * -> 명확한 Type비교가 선행되지 않는 이상, 단순히 절대 '=='으로 하면 안됨
             */
            logic(findMember5, findMember6);
            System.out.println("findMember6 instanceOf Member : " + (findMember6 instanceof Member));

            /**
             * 영속성 컨텍스트에 이미 entity가 저장되어있고, proxy의 target이 참조하고 있는 상황
             * + flush() / clear() 진행 안함
             */
            Member reference1 = em.getReference(Member.class, member1.getId()); //실제로는 findMember5와 같음
            System.out.println("reference1.getClass() = " + reference1.getClass());
            //reference1.getClass() = class jpashop.proxy.domain.Member -> 실제 Entity의 클래스

            /**
             * reference를 가져옴에도 proxy가 아닌 실제 객체를 가져오는 이유
             * 1. 이미 영속성 컨텍스트에 실제 entity가 저장되면, 굳이 Proxy를 가져와 얻을 수 있는 이점이 없으므로 실제 Entity를 가져옴
             * 2. *** JPA는 collection처럼 "==" 비교를 수월하게 하도록 설계되어 있음
             *    -> 한 TX내의 "==" 비교의 용이성/보장을 위해 Proxy를 가져오는 것이 아닌, 실제 객체 반환으로 설계되어있음
             *    *** 당연히 Proxy 객체 자체를 가져와서 == 비교한다면 타입 매치가 안됨
             */

            em.flush();
            em.clear();

            /**
             * 같은 Entity를 proxy로 가져오는 경우
             * - '같은 proxy 객체'를 가져온다
             * -> == 비교의 용이성!
             */
            Member reference2 = em.getReference(Member.class, member1.getId());
            Member reference3 = em.getReference(Member.class, member1.getId());
            System.out.println("reference2.getClass() = " + reference2.getClass());
            System.out.println("reference3.getClass() = " + reference3.getClass());
            System.out.println("reference2 == reference3 : " + (reference2 == reference3));
            //reference2.getClass() = class jpashop.proxy.domain.Member$HibernateProxy$RpxIdbBk
            //reference3.getClass() = class jpashop.proxy.domain.Member$HibernateProxy$RpxIdbBk
            //reference2 == reference3 : true

            /**
             * 실제 데이터 사용을위해 Proxy를 '초기화'하는 경우
             * *** 실제 객체로 '초기화'했더라도, "==" 비교의 용이성을 위해 Proxy객체를 그대로 사용한다! *** 매우매우 중요
             * -> 즉, Proxy와 실제 Entity의 구분 자체를 크게 신경쓰지 않도록, java의 collection처럼 설계된 부분들이 있으니, 이를 이해하자
             */
            reference2.getUsername();
            //더 확실한 예를 위해 em.find()로 가져오는 경우도 상정
            reference2 = em.find(Member.class, member1.getId());
            System.out.println("reference2.getClass() = " + reference2.getClass());
            System.out.println("reference3.getClass() = " + reference3.getClass());
            System.out.println("reference2 == reference3 : " + (reference2 == reference3));
            //reference2.getClass() = class jpashop.proxy.domain.Member$HibernateProxy$M3l8dYyS
            //reference3.getClass() = class jpashop.proxy.domain.Member$HibernateProxy$M3l8dYyS
            //reference2 == reference3 : true

            /**
             * 정리
             * 1. getReference()로 Proxy를 먼저 가져오는 경우 -> 초기화를 하더라도 Proxy를 그대로 유지
             * 2. find()를 통해 Entity를 먼저 가져오는 경우 -> Proxy를 사용하더라도 실제 Entity를 그대로 유지
             * -> 결론 : Proxy든 Entity든 상관없이 사용할 수 있지만, Type 체크가 필요한 경우에는 이를 유의하고 분리해서 생각할 필요가 있다.
             */

            /** 매우 중요 - 실무에서 많이 맞닥뜨리는 문제
             * 준영속 상태일 때, Proxy 초기화가 위험한 이유
             * 상황
             * 1. Proxy 객체는 '초기화'를 '영속성 컨텍스트'를 통해 요청
             * 2. Proxy 객체가 생성되어있고, 준영속화 - em.detach() / em.close() or em.clear() 로 영속성 컨텍스트를 비우거나 종료한 상황
             * -> 준영속/영속성컨텍스트에서 지워진 Proxy는 더 이상 도움을 받지 못함
             * 실무사례
             * - 주로 TX에 맞춰 영속성 컨텍스트를 관리
             * - 이미 끝난 TX의 Proxy객체를 사용하려 하면 초기화 이슈 발생
             */

            em.flush();
            em.clear();

            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember.getClass() = " + refMember.getClass()); //Proxy

            //영속성 컨텍스트에서 더이상 관리하지 않는 상황
            //em.detach(refMember);
            //em.close();
            //em.clear();

            //Proxy 객체 초기화 시도 -> 정보 조회를 위해 DB로 쿼리 나가야함
            refMember.getUsername();
            System.out.println("refMember.getUsername() = " + refMember.getUsername());
            System.out.println("refMember.getClass() = " + refMember.getClass());
            // -> 오류발생
            //org.hibernate.LazyInitializationException: *** could not initialize proxy [jpashop.proxy.domain.Member#1] - no Session

            em.flush();
            em.clear();

            /**
             * Proxy 확인을 도와주는 요소들
             * 1. proxy 인스턴스 초기화 여부 확인
             * - emf.getPersistenceUnitUtil().isLoaded(Object entity)
             * 2. Proxy 클래스 확인 방법
             * - getClass().getName()으로 출력
             * 3. Proxy 강제 초기화
             * - org.hibernate.Hibernate.initialize(entity)
             * - JPA 표준에는 강제 초기화가 없고, hibernate의 방식임
             * - 기존처럼 데이터를 직접 가져오는 '강제 호출'을 통해 초기화도 가능
             */

            Member refMember1 = em.getReference(Member.class, member1.getId());

            //1. proxy 인스턴스 초기화 여부 확인 - emf.getPersistenceUnitUtil().isLoaded(Object entity)
            System.out.println("isLoaded = : " + emf.getPersistenceUnitUtil().isLoaded(refMember1)); //초기화 안했으므로 false

            //강제 호출을 통한 초기화
            //refMember1.getUsername();
            //3. 강제 초기화 - org.hibernate.Hibernate.initialize(entity)
            Hibernate.initialize(refMember1); //강제 초기화

            System.out.println("isLoaded = : " + emf.getPersistenceUnitUtil().isLoaded(refMember1)); //true

            //2. Proxy 클래스 확인 - getClass().getName()으로 출력
            System.out.println("refMember1.getClass().getName() = " + refMember1.getClass().getName());
            //refMember1.getClass().getName() = jpashop.proxy.domain.Member$HibernateProxy$nt51SCmA
            //getReference()로 Proxy시 획득 후, 초기화가 일어났으므로 proxy 그대로 사용

            em.flush();
            em.clear();

            /**
             * LazyLoading
             * 상황 1.
             * 다시 돌아와서, 비즈니스 로직 상 Member를 조회할 때, Team을 가져오지 않고 Member만 조회할 수 없을까?
             * - @ManyToOne(fetch = FetchType.LAZY) - 지연로딩
             * - Member만 조회했음에도, Team은 Proxy 객체로 들고있음
             * - 이후 실제 DB 접근이 필요한 순간 영속성 컨텍스트를 통해 '초기화'가 이루어지고 DDL로 DB에 접근하고 '실제 Entity'가 참조됨
             *   + Proxy로 먼저 생성됐으므로, ** '초기화'로 실제 entity가 참조되어도, "==" 비교 용이를 위해 Proxy 타입을 들고있음
             *
             */

            Member findMemberA = em.find(Member.class, member1.getId());

            //1. fetch를 걸어놓은 상태에서 Member만 조회
            System.out.println("findMemberA.getTeam().getClass() = " + findMemberA.getTeam().getClass());
            //findMemberA.getTeam().getClass() = class jpashop.proxy.domain.TeamP$HibernateProxy$4TjO93HF
            // -> Team에 대한 정보는 Proxy로 생성해 들고있음
            
            System.out.println("================================================================");
        
            //2. 실제로 Team proxy에서 데이터 접근이 필요한 경우
            System.out.println("findMemberA.getTeam().getName() = " + findMemberA.getTeam().getName()); // 초기화가 일어나는 시점
            System.out.println("findMemberA.getTeam().getClass() = " + findMemberA.getTeam().getClass());
            //findMemberA.getTeam().getName() = team1
            //findMemberA.getTeam().getClass() = class jpashop.proxy.domain.TeamP$HibernateProxy$Sgmkoocp
            //영속성 컨텍스트의 '초기화'를 통해 DB에 쿼리를 수행하고, 실제 Entity를 구성해서 반환
            // + Proxy로 먼저 생성됐으므로, ** '초기화'로 실제 entity가 참조되어도, "==" 비교 용이를 위해 Proxy 타입을 들고있음

            /**LazyLoading
             * 상황2. 비즈니스 로직 상, Member와 Team을 동시에 조회하는 경우가 많다면?
             * - LazyLoading일 경우, SELECT 쿼리가 2번 나가므로 비효율적
             * - @ManyToOne(fetch = FetchType.EAGER) - 즉시로딩
             * - *** Member에 대한 SELECT 쿼리자체가 Team과 JOIN한 쿼리가 날아감
             *   -> 한번에 Member/Team을 한번에 땡겨오므로, Proxy가 필요없음
             * - Team의 class는 Proxy가 아닌 실제 Entity
             * - '초기화' 자체가 필요 없어짐 - Proxy가 아니므로
             */

            //findMemberA.getTeam().getClass() = class jpashop.proxy.domain.TeamP
            //================================================================
            //findMemberA.getTeam().getName() = team1
            //findMemberA.getTeam().getClass() = class jpashop.proxy.domain.TeamP

            /** 매우 중요!!!
             * Lazy/Eager Loading 주의점
             * !!! 실무에서는 'LazyLoading'만 사용 !!!
             * 1. 즉시 로딩을 사용하면 예상하지 못한 SQL이 발생
             *    - JOIN 테이블이 1~2개면 상관없지만, 실제로는 매우 많은 테이블이 연관관계로 구성되어있을 수 있다.
             * 2. 즉시 로딩은 JPQL에서 N+1 문제를 일으킨다.
             *    - ** em.find()는 명확한 PK를 사용하므로 JPA가 내부적으로 최적화 가능
             *    - JPQL은 그대로 SQL로 번역이 되므로 Member 모두 긁어옴
             *    -> 가져오니, Eager를 만남 -> 어라, Team 모두 다 긁어와야겠네
             *    -> Member의 개수 Team 정보를 채워 Eager를 만족시키기위해 별도의 쿼리를 다시 날림
             *    -> *** 즉, 1개의 쿼리를 날렸는데 추가 쿼리가 N개가 나가는 것을 N+1 문제라고 함
             * 해결 - 우선 모두 LazyLoading으로 세팅
             *      1. JPQL로 FETCH/JOIN을 통해 runtime 시점에 동적으로 구성해주는 방법 -> 주로 사용하게 될 것
             *      2. EntityGraph 어노테이션
             *      3. BatchSize
             * 3. @ManyToOne, @OneToOne의 X:1은 기본이 EAGER -> LAZY로 설정해줘야함
             *    + @OneToMany의 default는 LAZY
             */

            em.flush();
            em.clear();


            TeamP team2 = new TeamP();
            team2.setName("team2");
            em.persist(team2);

            member2 = em.find(Member.class, member2.getId());
            member2.changeTeam(team2);
            em.persist(member2);

            em.flush();
            em.clear();

            //JPQL에서의 문제
            //List<Member> members1 = em.createQuery("select m from Member m", Member.class)
            //        .getResultList();
            //1. JPQL은 그냥 SQL 날려버림 : SELECT * FROM MEMBER;
            //2. DB에서 모든 MEMBER를 긁어옴
            //3. EAGER -> TEAM도 가져와야겠네 : SELECT * FROM TEAM WHERE TEAM_ID = MEMBER.ID
            //실제 쿼리를 보면 Member를 가져온 후 Team을 가져오는 쿼리가 2번 나감

            //fetch join으로 해결하기
            List<Member> members1 = em.createQuery("select m from Member m join fetch m.team", Member.class)
                    .getResultList();
            // -> Member를 긁어오는 쿼리에 inner join으로 Team 정보도 가져옴

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }

    private static void logic(Member findMember5, Member findMember6) {
        System.out.println("findMember5 == findMember6 : " + (findMember5.getClass() == findMember6.getClass())); //false
    }

    // member만 조회하고 싶은데, Team의 정보까지 같이 가져온다면 비효율이지 않을까?
    private static void printMember(Member findMember1) {
        String username = findMember1.getUsername();
        System.out.println("member.getUsername = " + username);
    }

    private static void printMemberAndTeam(Member member) {
        String username = member.getUsername();
        System.out.println("member.getUsername() = " + username);
        TeamP findTeam = member.getTeam();
        System.out.println("findTeam.getName() = " + findTeam.getName());
    }
}
