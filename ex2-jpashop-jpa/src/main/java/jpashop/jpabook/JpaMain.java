package jpashop.jpabook;

import jpashop.jpabook.sampledomain.MemberA;
import jpashop.jpabook.sampledomain.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

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
            // -> FK 식별자를 직접 다루게 되어버림
            memberA.setTeamId(teamA.getId());

            em.persist(memberA);

            // *** H2DB에서는 AUTO일 경우 sequence를 공유하므로 memberA의 id가 2부터 시작하는 문제 발생
            // 지금 중요한 것은 아니니 신경쓰지 않아도 됨
            // -> 각각의 엔터티 테이블마다 id관리를 하고 싶다면 따로 식별자 매핑을 해주면 됨


            //2. 조회
            MemberA findMemberA1 = em.find(MemberA.class, memberA.getId());

            //조회한 Member의 Team을 알기 위해서는 DB와 통신하며 끄집어내야 함
            Long findTeamId = findMemberA1.getTeamId();
            Team findTeam = em.find(Team.class, findTeamId);
            // -> 연관관계가 없음 + 객체지향스럽지 않음 + '데이터' 중심의 모델링
            /** Table vs 객체
             * 'Table'은 FK를 통해 JOIN을 사용하고 연관된 '테이블'을 찾는다
             * '객체'는 '참조'를 사용해 연관된 '객체'를 찾는다.
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
