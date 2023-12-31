package jpashop.jpabook;

import jpashop.jpabook.OneToManySample.MemberB;
import jpashop.jpabook.OneToManySample.TeamB;
import jpashop.jpabook.domainV2.OrderItemV2;
import jpashop.jpabook.domainV2.OrderV2;
import jpashop.jpabook.sampledomain.MemberA;
import jpashop.jpabook.sampledomain.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            // * 객체를 테이블에 맞춰서 모델링한 경우의 사용

            // 1. persist()
            Team teamA = new Team();
            teamA.setName("TeamA");

            em.persist(teamA);

            // * 복습 - persist()를 통해 영속성 컨텍스트에 저장되면 '항상' PK값 존재
            // 그 전에 전략에 따라 DB 통신을 하든 말든, 결론적으로 영속성 컨텍스트에 저장된다면 PK값은 존재

            MemberA memberA = new MemberA();
            memberA.setUsername("memberA1");

            // *** 이 memberA를 Team에 소속시키고 싶은 경우
            //case1. FK 식별자를 직접 다루는 경우
            //memberA.setTeamId(teamA.getId());

            //case2. 엔터티/테이블 연관관계로 매핑이 완료된 경우
            memberA.setTeam(teamA);

            em.persist(memberA);

            // *** H2DB에서는 AUTO일 경우 sequence를 공유하므로 memberA의 id가 2부터 시작하는 문제 발생
            // 지금 중요한 것은 아니니 신경쓰지 않아도 됨
            // -> 각각의 엔터티 테이블마다 id관리를 하고 싶다면 따로 식별자 매핑을 해주면 됨

            /**
             * find()를 비롯한 1차캐시를 참고하는 경우, hibernate의 DB 쿼리를 보고 싶다면?
             * -> flush() + clear()
             * -> flush를 통해 영속성 컨텍스트의 1차캐시 + 저장소의 쿼리를 DB로 날리고
             * -> clear를 통해 영속성 컨텍스트 초기화
             */

            em.flush();
            em.clear();


            //2. 조회
            MemberA findMemberA1 = em.find(MemberA.class, memberA.getId());

            //case1. FK 식별자를 직접 다루는 경우
            //조회한 Member의 Team을 알기 위해서는 DB와 통신하며 끄집어내야 함
            //Long findTeamId = findMemberA1.getTeamId();
            //Team findTeam = em.find(Team.class, findTeamId);
            // -> 연관관계가 없음 + 객체지향스럽지 않음 + '데이터' 중심의 모델링
            /** Table vs 객체
             * 'Table'은 FK를 통해 JOIN을 사용하고 연관된 '테이블'을 찾는다
             * '객체'는 '참조'를 사용해 연관된 '객체'를 찾는다.
             */

            //case2. 엔터티/테이블 연관관계 매핑이 완료된 경우
            Team findTeam = findMemberA1.getTeam();
            System.out.println("findTeam.getName() = " + findTeam.getName());

            /* Hibernate:
                select
                    membera0_.MEMBERA_ID as MEMBERA_1_2_0_,
                    membera0_.TEAM_ID as TEAM_ID3_2_0_,
                    membera0_.USERNAME as USERNAME2_2_0_,
                    team1_.TEAM_ID as TEAM_ID1_5_1_,
                    team1_.name as name2_5_1_
                from
                    MemberA membera0_
                left outer join
                    Team team1_
                        on membera0_.TEAM_ID=team1_.TEAM_ID
                where
                    membera0_.MEMBERA_ID=? */

            // -> 다른 설정이 없다면 join을 통해 '한번에' 가져오지만, 이를 분리해서 가져오는 방식도 존재 - LazyLoading
            //    ex) @ManyToOne(fetch = FetchType.LAZY) / default는 FetchType.EAGER

            /**
             * 단방향의 특징
             * 1. Entity, 즉 객체 관점
             *    - Member 엔터티만 Team 엔터티의 참조를 들고있음
             *      Member -> Team O
             *      Team -> Member X
             * 2. Table, 즉 DB 관점
             *    - ****** Member의 FK(Team)와 Team의 PK가 이미 양방향으로 이어져있음
             *    -> 즉, ******* '방향성'이라는 것이 없다.
             * 그렇다면 Team을 통해서도 Member 참조에 접근하려면?
             * -> Entity를 연결만 해주면 양방향 연관관계로 설계해줄 수 있다
             * -> *** 즉, 객체는 '서로 다른' '단방향 연관관계 2개'
             */

            List<MemberA> findMembers = findMemberA1.getTeam().getMembers();
            for (MemberA findMember : findMembers) {
                System.out.println("findMember = " + findMember.getUsername());
            }

            em.flush();
            em.clear();

            /**
             * 양방향 연관관계 매핑 후 사용 시 가장 많이 하는 실수
             * 상황 - 새로운 member(N)를 team(1)에서 추가하려는 경우
             * 즉, FK를 들고있지 않은 객체에서, mappedBy로 연결된 필드를 통해 추가하는 경우
             */

            MemberA memberA2 = new MemberA();
            memberA2.setUsername("memberA2");
            em.persist(memberA2);

            Team teamA2 = new Team();
            teamA2.setName("TeamB");
            // 1. 연관관계 주인이 아닌 entity의 필드에서 추가 - 즉, 역방향만 연관관계 설정
            // -> Team 엔터티의 members 필드는 결국 읽기 전용이므로 update쿼리를 날리지 않음
            // *** 순수 객체 상태를 고려해 양 방향 모두 설정한 경우, flush/clear 없이 1차 캐시에서 가져오더라도 안전하게 사용 가능
            teamA2.getMembers().add(memberA2);
            em.persist(teamA2);

            // 2. 연관관계 주인인 entity의 필드에서 추가 - 즉, 정방향만 연관관계 설정
            memberA2.changeTeam(teamA2);

            /**
             * DB에서 member 테이블을 조회하면, TEAM_ID, 즉 FK가 업데이트 되지 않음
             */

            //em.flush();
            //em.clear();

            /**
             * 그렇다면, 객체지향적인 관점에서 하나의 객체에만 값을 설정해주는 것은 OOP스러운 것일까?
             * - 우선 살펴볼 상황 - 주인이 아닌 엔터티, 즉 값이 설정되지 않은 엔터티에서도 JPA가 처리해줬으므로 변경사항 반영 확인 가능
             * 그럼에도 문제가 생기는 2가지 부분
             * 1. 만약, em.flush() / em.clear()를 하지 않았을 경우
             *    -> 이미 1차캐시, 메모리에 존재하는 '순수한' 객체 정보를 처리하므로 주인 엔터티에서의 한 방향으로만 연관관계가 맺힌 것처럼 처리됨
             * + 테스트 케이스를 작성할 경우의 문제
             *    -> JPA 없이 순수한 java 코드 상태로 하는 경우가 많음
             * 해결
             * - 양방향 매핑을 할 경우, '순수 객체 상태'를 고려해서 양 방향 모두 값을 세팅해주는 것이 맞음
             * - *** 주인 객체의 set() 메서드 or 주인 아닌 객체의 add() 메서드를 처리해주면 실수하지 않을 가능성 높아짐 -> 연관관계 편의 메서드
             * - *** + 관례 상, setter의 문제점들이 있으니 메서드 이름을 change..()로 사용하는 것을 권장
             * - + 실제로는 이미 존재하는지의 여부 / null 체크 / 변경 시 기존 연관관계에서의 제거 등 다양한 처리가 추가적으로 이루어져야 함
             * - + 둘 중 한 곳에만 연관관계 편의 메서드를 만들어주는 것이 안전 -> 무한루프 걸릴 수도 있음
             *
             * 2. 양방향 매핑시 무한 루프 조심
             * - toString()메서드가 양 쪽에서 무한으로 호출할 수 있음 -> stackoverflow
             *   * lombok / json 생성 라이브러리 또한 toString()을 이런 구조로 자동으로 만들어버림
             * *** json 생성 라이브러리는 주로 Spring의 Controller에서 Entity를 직접 처리하며 json으로 변환할 때 이러한 문제가 발생함
             *
             * 해결
             * - 직접 만들든, lombok을 통한 toString() 자동 생성 사용할 때, 이 요소들을 고려하기
             * - json 생성 라이브러리 -> Controller에는 '절대 Entity를 그대로 반환하지 말 것'
             *   + Controller에서 Entity를 그대로 반환할 때의 문제점 2가지
             *     1. 위에서 다룬 문제
             *     2. Entity 변경 시, API 스펙 자체가 바뀌어 버리는 문제
             * -> Controller에서 Entity를 DTO를 활용해 값만 담아서 통신하는 것이 가장 안전
             *
             */

            Team findTeamB = em.find(Team.class, teamA2.getId());
            //flush/clear()를 하지 않았을 경우, 이미 1차캐시에 존재하는 순수한 객체의 정보를 그대로 처리 -> select 쿼리 안나감
            //flush/clear()를 했을 경우, JPA 내부 로직에 따라서 member/team을 DB에서 처리를 거쳐 가져오므로 1차 캐시의 순수한 객체 정보가 아닌 업데이트된 정보를 가져옴

            List<MemberA> findMembers2 = findTeamB.getMembers();

            System.out.println("======= 양방향 연관관계 주인인 엔터티에서 추가했을 때에도 생기는 문제 =======");
            for (MemberA a : findMembers2) {
                System.out.println("a.getUsername() = " + a.getUsername());
            }
            // 1번 문제
            // 1차캐시에 Team(1, 양방향 연관관계 주인이 아닌 객체)엔터티 정보가 남아있으면 아무 것도 출력되지 않음
            // -> 즉, 순수한 객체상태의 정보만 그대로 처리해버림

            // 2번 문제
            // toString() 호출 시 무한루프로 stackoverflow
            //System.out.println("members = " + findMembers2);

            System.out.println("======= 양방향 연관관계 주인인 엔터티에서 추가했을 때에도 생기는 문제 =======");

            em.flush();
            em.clear();

            /**
             * sudo 정도로만 구현해보기
             */
            OrderV2 orderV2 = new OrderV2();
            //1. 역방향으로 추가해준 방법을 사용
            orderV2.addOrderItem(new OrderItemV2());

            em.persist(orderV2);

            //2. 정방향으로 역방향 추가없이 그대로 사용
            OrderItemV2 orderItemV2 = new OrderItemV2();
            orderItemV2.setOrder(orderV2);

            em.persist(orderItemV2);
            /**
             * ***즉, 양뱡향 연관관계가 아니더라도, Application 개발에는 아무 문제가 없음
             * 그럼에도 사용하는 이유
             * 1. 개발상의 편의
             * 2. 연관관계 주인이 아닌 경우 조회밖에 할 수 없는 단점 극복을 위해 사용 - 이 부분 확실히 체크해보기
             * 3. JPQL에서 사용할 변수들을 위해
             */

            /**
             * '일대다' '단방향' 연관관계 매핑
             * - 객체관점 : 1 쪽에서 FK를 관리하는 경우
             * -> *** N 엔터티에서 따로 FK를 위한 필드를 가지고 있지 않아도, FK를 인지하고 row를 생성해 update 쿼리를 추가적으로 보내 구성해준다.
             * *** @JoinColumn을 사용하지 않는 경우 -> JoinTable을 추가적으로 생성해서 사용 ex) create table Team_Member
             * -> 이 경우 필드에 @JoinTable()을 통해 설정정보를 구성해줄수도 있음 - 추천하지 않음
             *
             * 단점
             * ***** Entity가 관리하는 FK가 다른 Table에 있음
             * 추가적인 UPDATE SQL 실행
             */
            /* 쿼리
            Hibernate:
                 create one-to-many row jpashop.jpabook.OneToManySample.TeamB.members
                 / update MEMBER_B  set  TEAM_B_ID=?  where   MEMBER_B_ID=? */

            MemberB memberB = new MemberB();
            memberB.setUsername("memberB");

            em.persist(memberB);

            TeamB teamB = new TeamB();
            teamB.setName("teamB");

            teamB.getMembers().add(memberB);

            em.persist(teamB);

            /**
             * JPA 스펙에서 지원함에도, 실무에서 이 일대다 연관관계를 사용하지 않는 이유
             * - 실제로 조작한 Entity와 다른 Entity에도 쿼리가 날아가므로 혼동스러울 수 있음
             * + * '다대일''양방향' 연관관계처럼 주인을 N 쪽에 잡으면, '객체적'으로 조금 손해를 볼 지라도(member -> team으로 실제로 조회할 일이 없더라도) 이러한 혼란을 제거
             * -> ORM보다 DB 테이블에 맞춰서 유지보수의 편의성을 높이는 trade off
             */

            /**
             * '일대다' '양방향' 연관관계
             * - JPA 스펙에서 지원 X
             * - N 엔터티의 @ManyToOne 컬럼에서 @JoinColumn(insertable = false, updatable = false)로 '읽기전용'으로 만들어줄 순 있음
             * - 어느 방향에서 FK 수정이 들어올지 모르면 예측이 불가하므로, 읽기만 하고 변경사항을 적용하지 못하도록 적용
             * - 실제로 DB에서 오류를 뱉어주지 않으므로 꼬였을 경우 찾기 힘들 수 있음
             */

            /** '일대일' '단방향' 연관관계
             * 다대일과 매우 유사
             * 1:1 연관관계도 다대일/일대다 연관관계처럼 mappedBy 동작은 같은 원리로 진행됨
             * -> 양방향(객체 관점에서는 서로 다른 단방향 2개 / 테이블 관점에서는 이미 양방향)으로 맺어주는 것은 매우 단순함
             * *** 주의점
             * '단방향' + 'FK가 주인이 아닌 위치에 존재'하는 경우는 JPA 스펙에서 지원도 안하고 설계 자체의 오류임
             * 즉, 대상 테이블에 외래키 단방향 연관관계는 불가
             * but, '양방향'이라면 지원해줌 -> 주 테이블에 외래키 양방향 정리하는 것 + 외래키가 반대쪽에만 있는것일 뿐!
             */

            /**
             * 그렇다면, '일대일' '단방향' 연관관계에서 FK는 어느 쪽이 들고 있도록 설계하는 것이 맞는가?
             * - 즉, '주 테이블에 FK' vs '대상 테이블에 FK'의 장단에서의 고민
             * 1. DB의 입장 - 이후의 확장성을 고려
             *    -> ex) member가 여러 개의 locker를 가질 수 있다면, locker에서 member를 들고, Unique를 빼주기만 하면 간단
             * 2. 개발자의 입장 - 당장의 select가 많이 이루어지는 '성능'을 고려할 수도 있다
             *    -> ex) DB 쿼리 하나로 member의 locker '유무' 정보까지 들고 있으므로, locker에서 join을 통한 쿼리보다 단순하고 쉽다
             * 치명적인 단점
             *    주 테이블에 FK를 두고 사용할 경우, 값이 없으면 null이 허용되므로 매우 신중히 처리해야한다
             * 결론
             *    '일대일'이 장기적으로도 유지가 된다면, 성능을 따라가는 것도 방법이다
             */

            /**
             * '일대일' 연관관계 정리
             * 1. 주 테이블에 외래키
             * - 조회만으로 대상 테이블의 데이터 유무 확인 가능
             * - 값이 없으면 FK에 null 허용
             * 2. 대상 테이블에 외래키
             * - 주 테이블과 대상 테이블의 관계를 '일대일'에서 '일대다' 관계로 전환할 때, 테이블 구조 유지 가능
             * - 프록시 기능의 한계 -> LazyLoading 으로 설정해도 항상 즉시 로딩됨
             *   -> 왜냐면, 프록시를 사용할 필요 없이, 무조건 대상 테이블을 조회해야 실제 데이터 유무를 알 수 있음
             *
             * *** FK 위치 따라서 양방향으로의 추가 구성 + 일대일 유지 요건 + 성능
             */

            /**
             * '다대다' 연관관계
             * 1. 테이블 관점
             * - '정규화'된 '관계형' 테이블 '2개'로는 '다대다' 표현이 불가능
             * - 연결 테이블 ex)TEAM_MEMBER 등을 통해 이를 통해 '다대일' 관계로 풀어내야함
             *
             * 2. 객체 관점
             * - 객체는 '컬렉션'을 활용해 '2개'의 객체로 서로의 list 등 컬렉션을 가짐으로써 '다대다' 관계 형성 가능
             *
             * -> Table에는 '연결 테이블'을 중간에 삽입한 구조로 객체와 테이블 매핑 지원
             * - @ManyTo@Many / @JoinTable로 연결지정 + '단방향 / '양방향' 모두 가능
             * but *** 사용하지 않는다.
             * - 1. 실무에선 다양한 추가적인 데이터들이 포함됨. 데이터 변경 시간 등등..
             * -    -> * 오로지 연결 테이블에는 '매핑 정보'만 들어갈 수 있고, 추가 정보를 사용할 수 없음
             * - 2. 숨겨진 연결 테이블에서 발생하는 쿼리를 예측하기 어렵다
             *
             * 극복
             * - 1. '연결 테이블'용 Entity를 추가해 승격
             *      -> 기존 Entity는 @ManyToMany -> @OneToMany 관계로 바뀜
             * - 2. 1번 + 기존 두 Entity를 묶어서 PK로 엮고 사용하는 방법 -> *** PK는 왠만하면 의미없는 값을 쓰는 것이 좋으므로 따로 구성
             *      -> 이 방법보단 PK는 따로 두고, FK로만 사용하는 방식이 나을수도. 왜냐하면 두 데이터가 묶이면 의미가 탄생 + 이후 추가에 대응 어려움
             * -> 즉, 1번 2번 방법은 각가의 tradeOff를 고려해서 선택 + 그러나 일관성있게 모든 테이블에 PK전략으로 따로 구성해주는 것이 안정적
             * -> ID의 종속성을 피하는 것이 미래의 확장성에 중요할수도
             *
             * 연결 테이블이 없이 사용할 때의 장점
             * - 주인이 아닌(FK가 없는) entity에서도 한 번의 조회로 상대 테이블의 데이터를 조회할 수 있다
             * - *** '일대일' 연관관계처럼 proxy/lazyLoading의 한계가 있는지는 확인해볼것
             */

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
